package kr.co.pincoin.api.domain.shop.model.order.enums;

import lombok.Getter;

@Getter
public enum PaymentAccount {
    KB(0),
    NH(1),
    SHINHAN(2),
    WOORI(3),
    IBK(4),
    PAYPAL(5);

    private final Integer value;

    PaymentAccount(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
