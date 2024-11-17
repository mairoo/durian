package kr.co.pincoin.api.domain.shop.model.order.enums;


public enum OrderVisibility {
    VISIBLE(1),
    HIDDEN(0);

    private final Integer value;

    OrderVisibility(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
