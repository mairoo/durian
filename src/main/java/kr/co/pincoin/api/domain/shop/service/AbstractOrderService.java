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

    private List<Product> validateAndGetProducts(List<OrderLineItem> items) {
        // 1. 중복 제거된 상품 ID 목록 추출 (동일 상품 여러 개 주문 시 한 번만 조회)
        Set<Long> uniqueProductIds = items.stream()
                .map(OrderLineItem::getProductId)
                .collect(Collectors.toSet());

        // 2. IN 절로 한 번에 상품 조회
        List<Product> products = productRepository.findAllByIdIn(uniqueProductIds);

        // 3. 상품 Map 생성 - Function.identity() 사용으로 불필요한 람다 표현식 제거
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 4. 주문 수량을 미리 집계 (동일 상품 여러 개 주문 시 총 수량 계산)
        Map<Long, Integer> quantityByProductId = items.stream()
                .collect(Collectors.groupingBy(OrderLineItem::getProductId,
                                               Collectors.summingInt(OrderLineItem::getQuantity)));

        // 5. 누락된 상품 확인
        if (products.size() != uniqueProductIds.size()) {
            Set<Long> foundProductIds = productMap.keySet();
            Set<Long> notFoundProductIds = new HashSet<>(uniqueProductIds);
            notFoundProductIds.removeAll(foundProductIds);

            throw new EntityNotFoundException("일부 상품을 찾을 수 없습니다: " + notFoundProductIds);
        }

        // 6. 상품 상태와 재고 한 번에 검증
        List<String> errors = new ArrayList<>();

        quantityByProductId.forEach((productId, totalQuantity) -> {
            Product product = productMap.get(productId);

            if (product.getStatus() == ProductStatus.DISABLED ||
                    product.getStock() == ProductStock.SOLD_OUT) {
                errors.add("판매 중인 상품이 아닙니다: " + productId);
            } else if (product.getStockQuantity() < totalQuantity) {
                errors.add(String.format(
                        "상품 '%s'의 재고가 부족합니다. 요청: %d, 현재 재고: %d",
                        product.getName(),
                        totalQuantity,
                        product.getStockQuantity()
                                        ));
            }
        });

        if (!errors.isEmpty()) {
            throw new EntityNotFoundException(String.join("\n", errors));
        }

        // 7. 원래 주문 순서대로 상품 리스트 생성
        return items.stream()
                .map(item -> productMap.get(item.getProductId()))
                .collect(Collectors.toList());
    }

    private String
    generateOrderNumber() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}