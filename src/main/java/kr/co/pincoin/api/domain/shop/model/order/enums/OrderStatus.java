package kr.co.pincoin.api.domain.shop.model.order.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING(0),
    COMPLETED(1),
    CANCELLED(2);

    private final Integer value;

    OrderStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
