package kr.co.pincoin.api.domain.shop.model.support.message.enums;

public enum NoticeCategory {
    COMMON(0),
    EVENT(1),
    PRICE(2);

    private final Integer value;

    NoticeCategory(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
