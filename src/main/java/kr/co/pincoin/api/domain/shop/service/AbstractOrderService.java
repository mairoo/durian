package kr.co.pincoin.api.domain.shop.service;

import jakarta.persistence.EntityNotFoundException;
import kr.co.pincoin.api.app.member.order.request.OrderCreateRequest;
import kr.co.pincoin.api.app.member.order.request.OrderLineItem;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
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

    protected final OrderProductRepository orderProductRepository;

    /**
     * 신규 주문
     */
    @Transactional
    protected Order
    createOrderInternal(User user,
                        OrderCreateRequest request,
                        ClientUtils.ClientInfo clientInfo) {
        // 1. 주문 상품 유효성 검증
        List<Product> products = validateAndGetProducts(request.getItems());

        // 2. 주문 총액 계산
        BigDecimal totalListPrice = BigDecimal.ZERO;
        BigDecimal totalSellingPrice = BigDecimal.ZERO;

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = request.getItems().get(i).getQuantity();

            totalListPrice = totalListPrice.add(product.getListPrice().multiply(BigDecimal.valueOf(quantity)));
            totalSellingPrice = totalSellingPrice.add(product.getSellingPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        // 3. Order 엔티티 생성
        Order order = Order.builder()
                // id 자동 생성
                .orderNo(generateOrderNumber())
                .fullname(user.getLastName() + user.getFirstName())
                .userAgent(clientInfo.getUserAgent())
                .acceptLanguage(clientInfo.getAcceptLanguage())
                .ipAddress(clientInfo.getIpAddress())
                .totalListPrice(totalListPrice)
                .totalSellingPrice(totalSellingPrice)
                .currency(OrderCurrency.KRW)
                .parent(null)  // 신규 주문 = null
                .user(user)
                // created, modified 자동 생성
                .paymentMethod(request.getPaymentMethod())
                .status(OrderStatus.PAYMENT_PENDING)
                .visibility(OrderVisibility.VISIBLE)
                .transactionId("") // 결제 전 null (페이팔 또는 PG 결제)
                .message("")
                .suspicious(false)
                .removed(false)
                .build();

        // 4. Order 즉시 저장하여 ID 확보
        Order savedOrder = orderRepository.saveAndFlush(order);

        // 5. OrderProduct 엔티티 생성 및 연결
        List<OrderProduct> orderProducts = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = request.getItems().get(i).getQuantity();

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

        // 6. OrderProduct 일괄 저장
        orderProductRepository.saveAll(orderProducts);

        // 5. 재고 확인 및 차감 (재고선점 문제는 향후 개선 사항)

        return savedOrder;
    }

    /**
     * 재주문
     */
    @Transactional
    public Order
    createReorderInternal(Integer userId, String orderNo, ClientUtils.ClientInfo clientInfo) {
        // 1. 주문상품 목록 조회 (order, user 포함)
        List<OrderProduct> originalOrderProducts = orderProductRepository
                .findAllByOrderNoAndUserIdFetchOrderAndUser(orderNo, userId);

        if (originalOrderProducts.isEmpty()) {
            throw new EntityNotFoundException("주문을 찾을 수 없습니다.");
        }

        // 2. 원본 주문 정보 (첫 번째 주문상품의 주문 정보 사용)
        Order originalOrder = originalOrderProducts.getFirst().getOrder();

        // 3. 상품 유효성 검증
        List<OrderLineItem> orderLineItems = originalOrderProducts.stream()
                .map(op -> new OrderLineItem(op.getCode(), op.getQuantity()))
                .collect(Collectors.toList());

        validateAndGetProducts(orderLineItems);

        // 4. 새로운 주문 생성
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
                .status(OrderStatus.PAYMENT_PENDING)
                .visibility(OrderVisibility.VISIBLE)
                .transactionId("")
                .message("")
                .suspicious(false)
                .removed(false)
                .build();

        Order savedOrder = orderRepository.saveAndFlush(newOrder);

        // 5. 주문상품 복사하여 새로 생성
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

    private List<Product>
    validateAndGetProducts(List<OrderLineItem> items) {
        // 1. 중복 제거된 상품 코드 목록 추출 (동일 상품 여러 개 주문 시 한 번만 조회)
        Set<String> uniqueProductCodes = items.stream()
                .map(OrderLineItem::getCode)
                .collect(Collectors.toSet());

        // 2. IN 절로 한 번에 상품 조회
        List<Product> products = productRepository.findAllByCodeIn(uniqueProductCodes);

        // 3. 상품 Map 생성 - 코드를 키로 사용
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getCode, Function.identity()));

        // 4. 주문 수량을 미리 집계 (동일 상품 여러 개 주문 시 총 수량 계산)
        Map<String, Integer> quantityByProductCode = items.stream()
                .collect(Collectors.groupingBy(OrderLineItem::getCode,
                                               Collectors.summingInt(OrderLineItem::getQuantity)));

        // 5. 누락된 상품 확인
        if (products.size() != uniqueProductCodes.size()) {
            Set<String> foundProductCodes = productMap.keySet();
            Set<String> notFoundProductCodes = new HashSet<>(uniqueProductCodes);
            notFoundProductCodes.removeAll(foundProductCodes);

            throw new EntityNotFoundException("일부 상품을 찾을 수 없습니다: " + String.join(", ",
                                                                                 notFoundProductCodes));
        }

        // 6. 상품 상태와 재고 한 번에 검증
        List<String> errors = new ArrayList<>();

        quantityByProductCode.forEach((code, totalQuantity) -> {
            Product product = productMap.get(code);

            if (product.getStatus() == ProductStatus.DISABLED || product.getStock() == ProductStock.SOLD_OUT) {
                errors.add("판매 중인 상품이 아닙니다: " + code);
            } else if (product.getStockQuantity() < totalQuantity) {
                errors.add(String.format("상품 '%s'의 재고가 부족합니다. 요청: %d, 현재 재고: %d",
                                         product.getName(),
                                         totalQuantity,
                                         product.getStockQuantity()));
            }
        });

        if (!errors.isEmpty()) {
            throw new IllegalStateException(String.join("\n", errors));
        }

        // 7. 원래 주문 순서대로 상품 리스트 생성
        return items.stream()
                .map(item -> productMap.get(item.getCode()))
                .collect(Collectors.toList());
    }

    private String
    generateOrderNumber() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}