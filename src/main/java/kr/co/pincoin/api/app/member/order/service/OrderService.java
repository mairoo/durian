package kr.co.pincoin.api.app.member.order.service;

import jakarta.persistence.EntityNotFoundException;
import kr.co.pincoin.api.app.member.order.request.OrderCreateRequest;
import kr.co.pincoin.api.app.member.order.request.OrderLineItem;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final OrderProductRepository orderProductRepository;

    /**
     * 내 주문 목록 조회
     */
    public Page<Order>
    getMyOrders(Integer userId,
                                   OrderSearchCondition condition,
                                   Pageable pageable) {
        return orderRepository.searchOrders(condition.withUserId(userId), pageable);
    }

    /**
     * 내 주문 조회
     */
    public Order
    getMyOrder(Integer userId,
                            String orderNo) {
        return orderRepository.findByOrderNoAndUserId(orderNo, userId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
    }

    /**
     * 내 주문 삭제 처리 (soft delete)
     */
    public void
    deleteMyOrder(Integer userId,
                              String orderNo) {
        Order order = orderRepository.findByOrderNoAndUserId(orderNo, userId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        // 이미 삭제된 주문인지 확인
        if (Boolean.TRUE.equals(order.getRemoved())) {
            throw new IllegalStateException("이미 삭제된 주문입니다.");
        }

        orderRepository.softDelete(order.getId());
    }

    /**
     * 내 주문 숨김 처리
     */
    public void
    hideMyOrder(Integer userId,
                            String orderNo) {
        Order order = orderRepository.findByOrderNoAndUserId(orderNo, userId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        // 이미 숨김 처리된 주문인지 확인
        if (OrderVisibility.HIDDEN.equals(order.getVisibility())) {
            throw new IllegalStateException("이미 숨김 처리된 주문입니다.");
        }

        order.updateVisibility(OrderVisibility.HIDDEN);
        orderRepository.save(order);
    }

    /**
     * 신규 주문
     */
    @Transactional
    public Order
    createOrder(User user,
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

    private List<Product>
    validateAndGetProducts(List<OrderLineItem> items) {
        // 1. 주문 상품 ID 목록 추출
        List<Long> productIds = items.stream()
                .map(OrderLineItem::getProductId)
                .collect(Collectors.toList());

        // 2. IN 절로 한 번에 상품 조회
        List<Product> products = productRepository.findAllByIdIn(productIds);

        // 3. 조회된 상품을 Map으로 변환하여 빠른 검색 가능하게 함
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        // 4. 모든 주문 상품이 존재하는지 확인
        if (products.size() != productIds.size()) {
            List<Long> notFoundProductIds = productIds.stream()
                    .filter(id -> !productMap.containsKey(id))
                    .toList();
            throw new EntityNotFoundException("일부 상품을 찾을 수 없습니다: " + notFoundProductIds);
        }

        // 5. 각 상품의 상태와 재고 확인
        List<Product> orderedProducts = new ArrayList<>();
        for (OrderLineItem item : items) {
            Product product = productMap.get(item.getProductId());

            // 상품 상태 확인
            if (product.getStatus() == ProductStatus.DISABLED || product.getStock() == ProductStock.SOLD_OUT) {
                throw new EntityNotFoundException("판매 중인 상품이 아닙니다: " + item.getProductId());
            }

            // 재고 확인
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new EntityNotFoundException(
                        String.format("상품 '%s'의 재고가 부족합니다. 요청: %d, 현재 재고: %d",
                                      product.getName(),
                                      item.getQuantity(),
                                      product.getStockQuantity()));
            }

            orderedProducts.add(product);
        }

        return orderedProducts;
    }

    private String
    generateOrderNumber() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
