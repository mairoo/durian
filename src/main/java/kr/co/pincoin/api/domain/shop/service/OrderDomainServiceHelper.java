package kr.co.pincoin.api.domain.shop.service;

import jakarta.persistence.EntityNotFoundException;
import kr.co.pincoin.api.app.member.order.request.OrderLineItem;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.infra.shop.service.OrderPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class OrderDomainServiceHelper {
    private final OrderPersistenceService persistenceService;

    /**
     * 주문 생성 관련 헬퍼 메소드
     */
    String generateOrderNumber() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    List<Product> validateProductsForOrder(List<OrderLineItem> items) {
        List<Product> products = persistenceService.findProducts(items);

        Set<String> requestedCodes = items.stream()
                .map(OrderLineItem::getCode)
                .collect(Collectors.toSet());
        Set<String> foundCodes = products.stream()
                .map(Product::getCode)
                .collect(Collectors.toSet());

        if (!foundCodes.containsAll(requestedCodes)) {
            Set<String> notFoundCodes = new HashSet<>(requestedCodes);
            notFoundCodes.removeAll(foundCodes);
            throw new EntityNotFoundException("상품을 찾을 수 없습니다: " + String.join(", ", notFoundCodes));
        }

        Map<String, Integer> quantityByCode = items.stream()
                .collect(Collectors.groupingBy(
                        OrderLineItem::getCode,
                        Collectors.summingInt(OrderLineItem::getQuantity)));

        List<String> errors = new ArrayList<>();
        for (Product product : products) {
            Integer requestedQuantity = quantityByCode.get(product.getCode());
            if (product.getStatus() == ProductStatus.DISABLED ||
                    product.getStock() == ProductStock.SOLD_OUT) {
                errors.add("판매 중인 상품이 아닙니다: " + product.getCode());
            } else if (product.getStockQuantity() < requestedQuantity) {
                errors.add(String.format(
                        "상품 '%s'의 재고가 부족합니다. 요청: %d, 재고: %d",
                        product.getName(), requestedQuantity, product.getStockQuantity()));
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalStateException(String.join("\n", errors));
        }

        return products;
    }

    /**
     * 주문 상품 관련 헬퍼 메소드
     */
    List<OrderProduct> createOrderProducts(List<Product> products,
                                           List<OrderLineItem> items,
                                           Order order) {
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getCode, Function.identity()));

        return items.stream()
                .map(item -> {
                    Product product = productMap.get(item.getCode());
                    return OrderProduct.of(
                            product.getName(),
                            product.getSubtitle(),
                            product.getCode(),
                            product.getListPrice(),
                            product.getSellingPrice(),
                            item.getQuantity(),
                            order);
                })
                .collect(Collectors.toList());
    }

    List<OrderProduct> copyOrderProducts(List<OrderProduct> originalProducts, Order newOrder) {
        return originalProducts.stream()
                .map(op -> OrderProduct.of(
                        op.getName(),
                        op.getSubtitle(),
                        op.getCode(),
                        op.getListPrice(),
                        op.getSellingPrice(),
                        op.getQuantity(),
                        newOrder))
                .collect(Collectors.toList());
    }

    /**
     * 가격 계산 관련 헬퍼 메소드
     */
    BigDecimal calculateTotalPrice(List<Product> products,
                                   List<OrderLineItem> items,
                                   Function<Product, BigDecimal> priceExtractor) {
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getCode, Function.identity()));

        return items.stream()
                .map(item -> {
                    Product product = productMap.get(item.getCode());
                    return priceExtractor.apply(product)
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    BigDecimal calculateTotalPayments(Order order) {
        return persistenceService.findPaymentsByOrder(order)
                .stream()
                .map(OrderPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    boolean isPaymentCompleted(BigDecimal totalPayments, BigDecimal orderAmount) {
        return totalPayments.compareTo(orderAmount) >= 0;
    }

    /**
     * 바우처 관련 헬퍼 메소드
     */
    Product validateProductForVoucherIssue(OrderProduct orderProduct) {
        return persistenceService.findProducts(List.of(
                        new OrderLineItem(orderProduct.getCode(), orderProduct.getQuantity())))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "상품을 찾을 수 없습니다: " + orderProduct.getCode()));
    }

    void validateVouchersAvailability(OrderProduct orderProduct,
                                      List<Voucher> vouchers,
                                      Product product) {
        if (vouchers.size() < orderProduct.getQuantity()) {
            throw new IllegalStateException(
                    String.format("상품 '%s'의 사용 가능한 상품권이 부족합니다. 필요: %d, 가용: %d",
                                  orderProduct.getName(),
                                  orderProduct.getQuantity(),
                                  vouchers.size()));
        }

        if (product.getStockQuantity() < vouchers.size()) {
            throw new IllegalStateException(
                    String.format("상품 '%s'의 재고와 상품권 수량이 불일치합니다. 재고: %d, 상품권: %d",
                                  product.getName(),
                                  product.getStockQuantity(),
                                  vouchers.size()));
        }
    }

    void revokeVouchers(Long orderId) {
        List<OrderProductVoucher> vouchers = persistenceService.findOrderProductVouchers(orderId);

        vouchers.forEach(voucher -> {
            voucher.revoke();

            Voucher originalVoucher = persistenceService.findVoucherByCode(voucher.getCode())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "상품권을 찾을 수 없습니다: " + voucher.getCode()));

            originalVoucher.markAsPurchased();
            persistenceService.updateVoucher(originalVoucher);
        });

        persistenceService.saveOrderProductVouchers(vouchers);
    }

    /**
     * 상태 변경 관련 헬퍼 메소드
     */
    void validateRefundRequest(Order order) {
        if (order.getStatus() == OrderStatus.REFUND_REQUESTED ||
                order.getStatus() == OrderStatus.REFUND_PENDING ||
                order.getStatus() == OrderStatus.REFUNDED1 ||
                order.getStatus() == OrderStatus.REFUNDED2) {
            throw new IllegalStateException("이미 환불 처리된 주문입니다.");
        }
    }

    void validateRefundCompletion(Order refundOrder) {
        if (refundOrder.getStatus() != OrderStatus.REFUND_PENDING) {
            throw new IllegalStateException("환불 처리 대기 상태의 주문이 아닙니다.");
        }

        Order originalOrder = refundOrder.getParent();
        if (originalOrder == null) {
            throw new IllegalStateException("환불 처리할 원본 주문을 찾을 수 없습니다.");
        }

        if (originalOrder.getStatus() != OrderStatus.REFUND_REQUESTED) {
            throw new IllegalStateException("환불 요청 상태의 주문이 아닙니다.");
        }
    }

    void updateOrderStatusAfterPayment(Order order, Profile profile) {
        order.updateStatus(profile.isPhoneVerified() && profile.isDocumentVerified()
                                   ? OrderStatus.PAYMENT_VERIFIED
                                   : OrderStatus.UNDER_REVIEW);
    }
}