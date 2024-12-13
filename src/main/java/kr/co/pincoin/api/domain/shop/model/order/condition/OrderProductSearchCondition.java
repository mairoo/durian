package kr.co.pincoin.api.domain.shop.model.order.condition;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderProductSearchCondition {

    private final Long orderId;
    private final String orderNo;
    private final Integer userId;

    @Builder
    private OrderProductSearchCondition(
        Long orderId,
        String orderNo,
        Integer userId) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
    }

    // 정적 팩토리 메서드들
    public static OrderProductSearchCondition empty() {
        return OrderProductSearchCondition.builder().build();
    }

    public static OrderProductSearchCondition ofOrderId(Long orderId) {
        return OrderProductSearchCondition.builder()
            .orderId(orderId)
            .build();
    }

    public static OrderProductSearchCondition ofOrderNo(String orderNo) {
        return OrderProductSearchCondition.builder()
            .orderNo(orderNo)
            .build();
    }

    public static OrderProductSearchCondition ofUserId(Integer userId) {
        return OrderProductSearchCondition.builder()
            .userId(userId)
            .build();
    }

    public OrderProductSearchCondition withOrderId(Long orderId) {
        return OrderProductSearchCondition.builder()
            .orderId(orderId)
            .orderNo(this.orderNo)
            .userId(this.userId)
            .build();
    }

    public OrderProductSearchCondition withUserId(Integer userId) {
        return OrderProductSearchCondition.builder()
            .orderId(this.orderId)
            .orderNo(this.orderNo)
            .userId(userId)
            .build();
    }

    // 유틸리티 메서드들
    public boolean hasOrderId() {
        return orderId != null;
    }

    public boolean hasOrderNo() {
        return orderNo != null && !orderNo.isBlank();
    }

    public boolean hasUserId() {
        return userId != null;
    }
}