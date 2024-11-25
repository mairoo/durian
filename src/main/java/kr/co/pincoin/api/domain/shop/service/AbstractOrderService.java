package kr.co.pincoin.api.domain.shop.service;

import jakarta.persistence.EntityNotFoundException;
import kr.co.pincoin.api.app.member.order.request.OrderCreateRequest;
import kr.co.pincoin.api.app.member.order.request.OrderLineItem;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.profile.ProfileRepository;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.domain.shop.repository.order.OrderPaymentRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import kr.co.pincoin.api.global.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractOrderService {
    protected final OrderRepository orderRepository;

    protected final ProductRepository productRepository;

    protected final OrderPaymentRepository orderPaymentRepository;

    protected final OrderProductRepository orderProductRepository;

    protected final OrderProductVoucherRepository orderProductVoucherRepository;

    protected final VoucherRepository voucherRepository;

    protected final ProfileRepository profileRepository;

    /**
     * 신규 주문을 생성하는 메소드
     *
     * @param user 주문하는 사용자 정보
     * @param request 주문 생성 요청 정보 (상품 목록, 결제 수단 등)
     * @param clientInfo 클라이언트 환경 정보 (IP, User-Agent, Accept-Language)
     * @return 생성된 주문 엔티티
     * @throws EntityNotFoundException 주문할 상품이 존재하지 않는 경우
     * @throws IllegalStateException 상품 재고가 부족하거나 판매 불가능한 경우
     */
    @Transactional
    protected Order
    createOrderInternal(User user,
                        OrderCreateRequest request,
                        ClientUtils.ClientInfo clientInfo) {
        // 주문할 상품들의 존재 여부, 상태, 재고를 검증하고 상품 정보 조회
        List<Product> products = validateAndGetProducts(request.getItems());

        // 주문 총액 계산 (정가, 실제 판매가)
        BigDecimal totalListPrice = BigDecimal.ZERO;
        BigDecimal totalSellingPrice = BigDecimal.ZERO;

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = request.getItems().get(i).getQuantity();

            // 각 상품의 수량을 곱한 금액을 누적 계산
            totalListPrice = totalListPrice.add(product.getListPrice().multiply(BigDecimal.valueOf(quantity)));
            totalSellingPrice = totalSellingPrice.add(product.getSellingPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        // 주문 기본 정보 설정 및 엔티티 생성
        Order order = Order.builder()
                .orderNo(generateOrderNumber())         // UUID 기반 주문번호 생성
                .fullname(user.getLastName() + user.getFirstName())
                .userAgent(clientInfo.getUserAgent())   // 사용자 브라우저/기기 정보
                .acceptLanguage(clientInfo.getAcceptLanguage())  // 선호 언어
                .ipAddress(clientInfo.getIpAddress())   // 주문자 IP
                .totalListPrice(totalListPrice)         // 정가 기준 총액
                .totalSellingPrice(totalSellingPrice)   // 실제 판매가 기준 총액
                .currency(OrderCurrency.KRW)            // 원화 주문
                .parent(null)                           // 신규 주문은 부모 주문 없음
                .user(user)                             // 주문자 정보
                .paymentMethod(request.getPaymentMethod())  // 결제 수단
                .status(OrderStatus.PAYMENT_PENDING)    // 초기 상태: 결제 대기
                .visibility(OrderVisibility.VISIBLE)    // 주문 노출 여부
                .transactionId("")                      // 결제 전이므로 거래 ID 없음
                .message("")
                .suspicious(false)                      // 이상 거래 표시
                .removed(false)                         // 삭제 표시
                .build();

        // 주문 저장 후 생성된 ID 획득 (주문상품 연결을 위해 필요)
        Order savedOrder = orderRepository.saveAndFlush(order);

        // 주문에 포함된 각 상품들의 주문상품 정보 생성
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = request.getItems().get(i).getQuantity();

            // 주문 시점의 상품 정보를 복사하여 주문상품 생성
            OrderProduct orderProduct = OrderProduct.of(
                    product.getName(),
                    product.getSubtitle(),
                    product.getCode(),
                    product.getListPrice(),
                    product.getSellingPrice(),
                    quantity,
                    savedOrder);

            orderProducts.add(orderProduct);
        }

        // 생성된 주문상품들 일괄 저장
        orderProductRepository.saveAll(orderProducts);

        return savedOrder;
    }

    /**
     * 기존 주문을 기반으로 재주문을 생성하는 메소드
     *
     * @param userId 재주문을 요청한 사용자 ID
     * @param orderNo 원본 주문번호
     * @param clientInfo 클라이언트 환경 정보
     * @return 생성된 재주문 엔티티
     * @throws EntityNotFoundException 원본 주문이 존재하지 않는 경우
     * @throws IllegalStateException 상품 재고가 부족하거나 판매 불가능한 경우
     */
    @Transactional
    public Order
    createReorderInternal(Integer userId,
                          String orderNo,
                          ClientUtils.ClientInfo clientInfo) {
        // 원본 주문의 상품 목록과 주문/사용자 정보를 한 번에 조회
        List<OrderProduct> originalOrderProducts = orderProductRepository
                .findAllByOrderNoAndUserIdFetchOrderAndUser(orderNo, userId);

        if (originalOrderProducts.isEmpty()) {
            throw new EntityNotFoundException("주문을 찾을 수 없습니다.");
        }

        // 첫 번째 주문상품에서 원본 주문 정보 획득
        Order originalOrder = originalOrderProducts.getFirst().getOrder();

        // 원본 주문상품 목록을 OrderLineItem으로 변환하여 재고/상태 검증
        List<OrderLineItem> orderLineItems = originalOrderProducts.stream()
                .map(op -> new OrderLineItem(op.getCode(), op.getQuantity()))
                .collect(Collectors.toList());
        validateAndGetProducts(orderLineItems);

        // 원본 주문 정보를 기반으로 새 주문 생성 (고객 정보, 금액 등 복사)
        Order newOrder = Order.builder()
                .orderNo(generateOrderNumber())
                .fullname(originalOrder.getFullname())
                .userAgent(clientInfo.getUserAgent())
                .acceptLanguage(clientInfo.getAcceptLanguage())
                .ipAddress(clientInfo.getIpAddress())
                .totalListPrice(originalOrder.getTotalListPrice())
                .totalSellingPrice(originalOrder.getTotalSellingPrice())
                .currency(originalOrder.getCurrency())
                .user(originalOrder.getUser())
                .paymentMethod(originalOrder.getPaymentMethod())
                .status(OrderStatus.PAYMENT_PENDING)    // 초기 상태: 결제 대기
                .visibility(OrderVisibility.VISIBLE)
                .transactionId("")                      // 신규 주문이므로 거래 ID 초기화
                .message("")
                .suspicious(false)
                .removed(false)
                .build();

        Order savedOrder = orderRepository.saveAndFlush(newOrder);

        // 원본 주문상품들을 새 주문에 복사하여 연결
        List<OrderProduct> newOrderProducts = originalOrderProducts.stream()
                .map(op -> OrderProduct.of(
                        op.getName(),
                        op.getSubtitle(),
                        op.getCode(),
                        op.getListPrice(),
                        op.getSellingPrice(),
                        op.getQuantity(),
                        savedOrder))
                .collect(Collectors.toList());

        orderProductRepository.saveAll(newOrderProducts);

        return savedOrder;
    }

    /**
     * 주문에 대한 상품권을 발행하고 재고를 차감하는 메소드
     *
     * @param order 상품권을 발행할 주문
     * @return 상품권이 발행된 주문
     * @throws EntityNotFoundException 상품이 존재하지 않는 경우
     * @throws IllegalStateException   상품권 수량 부족 또는 재고 불일치 시
     */
    @Transactional
    protected Order
    issueVouchersInternal(Order order) {
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderFetchOrder(order);

        for (OrderProduct orderProduct : orderProducts) {
            Product product = productRepository.findByCode(orderProduct.getCode())
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + orderProduct.getCode()));

            // 해당 상품의 미사용(PURCHASED) 상품권 중 필요 수량만큼 조회
            List<Voucher> availableVouchers = voucherRepository
                    .findTopNByProductCodeAndStatusOrderByIdAsc(
                            orderProduct.getCode(),
                            VoucherStatus.PURCHASED,
                            orderProduct.getQuantity());

            // 구매 수량만큼 상품권이 존재하는지 검증
            if (availableVouchers.size() < orderProduct.getQuantity()) {
                throw new IllegalStateException(
                        String.format("상품 '%s'의 사용 가능한 상품권이 부족합니다. 필요: %d, 가용: %d",
                                      orderProduct.getName(),
                                      orderProduct.getQuantity(),
                                      availableVouchers.size())
                );
            }

            // 상품의 실제 재고와 사용 가능한 상품권 수량이 일치하는지 검증
            if (product.getStockQuantity() < availableVouchers.size()) {
                throw new IllegalStateException(
                        String.format("상품 '%s'의 재고와 상품권 수량이 불일치합니다. 재고: %d, 상품권: %d",
                                      product.getName(),
                                      product.getStockQuantity(),
                                      availableVouchers.size())
                );
            }

            // 상품권별 주문상품 연결정보 생성 및 상태 변경
            List<OrderProductVoucher> orderProductVouchers = new ArrayList<>();
            for (Voucher voucher : availableVouchers) {
                voucher.markAsSold();  // 상품권 상태를 SOLD로 변경

                orderProductVouchers.add(OrderProductVoucher.builder()
                                                 .orderProduct(orderProduct)
                                                 .code(voucher.getCode())
                                                 .remarks(voucher.getRemarks())
                                                 .revoked(false)
                                                 .build());
            }

            orderProductVoucherRepository.saveAll(orderProductVouchers);

            // 상품 재고 차감 처리
            product.updateStockQuantity(product.getStockQuantity() - orderProduct.getQuantity());
            productRepository.save(product);
        }

        return order;
    }

    /**
     * 주문에 대한 환불 요청을 처리하는 메소드
     *
     * @param user    환불을 요청한 사용자
     * @param order   환불할 주문
     * @param message 환불 사유
     * @return 생성된 환불 주문
     * @throws IllegalStateException 이미 환불 처리된 주문인 경우
     */
    @Transactional
    protected Order
    refundRequestInternal(User user,
                          Order order,
                          String message) {
        // 환불 가능한 상태인지 검증
        if (order.getStatus() == OrderStatus.REFUND_REQUESTED
                || order.getStatus() == OrderStatus.REFUND_PENDING
                || order.getStatus() == OrderStatus.REFUNDED1
                || order.getStatus() == OrderStatus.REFUNDED2) {
            throw new IllegalStateException("이미 환불 처리된 주문입니다.");
        }

        // 원본 주문을 환불 요청 상태로 변경
        order.updateStatus(OrderStatus.REFUND_REQUESTED);
        order.updateMessage(message);
        orderRepository.save(order);

        // 원본 주문 정보를 기반으로 환불 주문 생성
        Order refundOrder = Order.builder()
                .orderNo(generateOrderNumber())
                .fullname(order.getFullname())
                .userAgent(order.getUserAgent())
                .acceptLanguage(order.getAcceptLanguage())
                .ipAddress(order.getIpAddress())
                .totalListPrice(order.getTotalListPrice())
                .totalSellingPrice(order.getTotalSellingPrice())
                .currency(order.getCurrency())
                .parent(order)                         // 환불 주문은 원본 주문을 부모로 설정
                .user(user)
                .paymentMethod(order.getPaymentMethod())
                .status(OrderStatus.REFUND_PENDING)    // 초기 상태: 환불 대기
                .visibility(OrderVisibility.VISIBLE)
                .transactionId("")
                .message(message)                      // 환불 사유
                .suspicious(false)
                .removed(false)
                .build();

        Order savedRefundOrder = orderRepository.saveAndFlush(refundOrder);

        // 원본 주문상품을 환불 주문에 복사
        List<OrderProduct> originalOrderProducts = orderProductRepository.findAllByOrderFetchOrder(order);
        List<OrderProduct> refundOrderProducts = originalOrderProducts.stream()
                .map(op -> OrderProduct.of(
                        op.getName(),
                        op.getSubtitle(),
                        op.getCode(),
                        op.getListPrice(),
                        op.getSellingPrice(),
                        op.getQuantity(),
                        savedRefundOrder))
                .collect(Collectors.toList());

        orderProductRepository.saveAll(refundOrderProducts);

        // 발행된 상품권들을 무효화하고 상태 변경
        List<OrderProductVoucher> vouchers = orderProductVoucherRepository
                .findAllByOrderProductOrderId(order.getId());

        vouchers.forEach(voucher -> {
            voucher.revoke();  // 주문상품-상품권 연결 무효화

            // 상품권 상태를 다시 PURCHASED로 변경 (재사용 가능)
            Voucher originalVoucher = voucherRepository.findByCode(voucher.getCode())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "상품권을 찾을 수 없습니다: " + voucher.getCode()));

            originalVoucher.markAsPurchased();
            voucherRepository.save(originalVoucher);
        });

        orderProductVoucherRepository.saveAll(vouchers);

        return savedRefundOrder;
    }

    /**
     * 환불 처리를 완료하는 메소드
     *
     * @param refundOrder 처리할 환불 주문
     * @return 완료된 환불 주문
     * @throws IllegalStateException 주문 상태가 유효하지 않은 경우
     */
    @Transactional
    protected Order
    completeRefundInternal(Order refundOrder) {
        // 환불 주문이 대기 상태인지 확인
        if (refundOrder.getStatus() != OrderStatus.REFUND_PENDING) {
            throw new IllegalStateException("환불 처리 대기 상태의 주문이 아닙니다.");
        }

        // 원본 주문 조회 및 존재 여부 검증
        Order originalOrder = Optional.ofNullable(refundOrder.getParent())
                .orElseThrow(() -> new IllegalStateException("환불 처리할 원본 주문을 찾을 수 없습니다."));

        // 원본 주문이 환불 요청 상태인지 확인
        if (originalOrder.getStatus() != OrderStatus.REFUND_REQUESTED) {
            throw new IllegalStateException("환불 요청 상태의 주문이 아닙니다.");
        }

        // 원본 주문과 환불 주문의 상태를 환불 완료로 변경
        originalOrder.updateStatus(OrderStatus.REFUNDED1);  // 원본 주문 환불 완료
        refundOrder.updateStatus(OrderStatus.REFUNDED2);   // 환불 주문 처리 완료

        // 변경된 상태 저장
        orderRepository.save(originalOrder);
        orderRepository.save(refundOrder);

        return refundOrder;
    }

    /**
     * 주문에 결제 정보를 추가하고 상태를 업데이트하는 메소드
     *
     * @param orderId 결제할 주문 ID
     * @param payment 결제 정보
     * @return 저장된 결제 정보
     * @throws EntityNotFoundException 주문 또는 사용자 프로필을 찾을 수 없는 경우
     */
    @Transactional
    public OrderPayment addPayment(Long orderId, OrderPayment payment) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        OrderPayment savedPayment = orderPaymentRepository.save(payment);

        // 주문의 총 결제금액 계산
        BigDecimal totalPayments = orderPaymentRepository.findByOrderAndIsRemovedFalse(order)
                .stream()
                .map(OrderPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 주문금액 결제 완료시 본인인증 여부에 따른 상태 변경
        if (totalPayments.compareTo(order.getTotalSellingPrice()) >= 0) {
            Profile profile = profileRepository.findByUserWithFetch(order.getUser())
                    .orElseThrow(() -> new EntityNotFoundException("사용자 프로필을 찾을 수 없습니다."));

            order.updateStatus(profile.isPhoneVerified() && profile.isDocumentVerified()
                                       ? OrderStatus.PAYMENT_VERIFIED
                                       : OrderStatus.UNDER_REVIEW);

            orderRepository.save(order);
        }

        return savedPayment;
    }

    /**
     * 결제 정보를 삭제하고 주문 상태를 업데이트하는 메소드
     *
     * @param paymentId 삭제할 결제 ID
     * @throws EntityNotFoundException 결제 정보를 찾을 수 없는 경우
     */
    @Transactional
    public void deletePayment(Long paymentId) {
        OrderPayment payment = orderPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));

        Order order = payment.getOrder();

        payment.remove(); // soft delete
        orderPaymentRepository.save(payment);

        BigDecimal totalPayments = orderPaymentRepository.findByOrderAndIsRemovedFalse(order)
                .stream()
                .map(OrderPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPayments.compareTo(order.getTotalSellingPrice()) < 0) {
            order.updateStatus(OrderStatus.PAYMENT_PENDING);
            orderRepository.save(order);
        }
    }

    /**
     * 주문 상품들의 유효성을 검증하고 상품 정보를 조회하는 메소드
     *
     * @param items 검증할 주문 상품 목록
     * @return 검증된 상품 엔티티 목록 (원본 주문 순서 유지)
     * @throws EntityNotFoundException 상품이 존재하지 않는 경우
     * @throws IllegalStateException   상품 상태가 유효하지 않거나 재고가 부족한 경우
     */
    private List<Product>
    validateAndGetProducts(List<OrderLineItem> items) {
        // 중복 제거된 상품 코드 추출 (DB 조회 최소화)
        Set<String> uniqueProductCodes = items.stream()
                .map(OrderLineItem::getCode)
                .collect(Collectors.toSet());

        // 상품 일괄 조회
        List<Product> products = productRepository.findAllByCodeIn(uniqueProductCodes);

        // 상품 조회 결과를 Map으로 변환 (코드 -> 상품)
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getCode, Function.identity()));

        // 상품별 총 주문 수량 집계 (동일 상품 여러개 주문 고려)
        Map<String, Integer> quantityByProductCode = items.stream()
                .collect(Collectors.groupingBy(OrderLineItem::getCode,
                                               Collectors.summingInt(OrderLineItem::getQuantity)));

        // 누락된 상품 검증
        if (products.size() != uniqueProductCodes.size()) {
            Set<String> foundProductCodes = productMap.keySet();
            Set<String> notFoundProductCodes = new HashSet<>(uniqueProductCodes);
            notFoundProductCodes.removeAll(foundProductCodes);

            throw new EntityNotFoundException("일부 상품을 찾을 수 없습니다: " +
                                                      String.join(", ", notFoundProductCodes));
        }

        // 상품 상태 및 재고 검증
        List<String> errors = new ArrayList<>();
        quantityByProductCode.forEach((code, totalQuantity) -> {
            Product product = productMap.get(code);

            if (product.getStatus() == ProductStatus.DISABLED ||
                    product.getStock() == ProductStock.SOLD_OUT) {
                errors.add("판매 중인 상품이 아닙니다: " + code);
            } else if (product.getStockQuantity() < totalQuantity) {
                errors.add(String.format("상품 '%s'의 재고가 부족합니다. 요청: %d, 현재 재고: %d",
                                         product.getName(), totalQuantity, product.getStockQuantity()));
            }
        });

        if (!errors.isEmpty()) {
            throw new IllegalStateException(String.join("\n", errors));
        }

        // 원본 주문의 상품 순서 유지하여 반환
        return items.stream()
                .map(item -> productMap.get(item.getCode()))
                .collect(Collectors.toList());
    }

    /**
     * UUID 기반의 32자리 주문번호를 생성하는 메소드
     *
     * @return 생성된 주문번호 (하이픈 제거된 UUID)
     */
    private String generateOrderNumber() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}