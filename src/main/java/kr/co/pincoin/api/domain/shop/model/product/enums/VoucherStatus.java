package kr.co.pincoin.api.domain.shop.model.product.enums;

import lombok.Getter;

@Getter
public enum VoucherStatus {
    PURCHASED(0),
    SOLD(1),
    REVOKED(2);

    private final Integer value;

    VoucherStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
