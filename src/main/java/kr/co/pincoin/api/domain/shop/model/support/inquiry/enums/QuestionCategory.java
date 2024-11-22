package kr.co.pincoin.api.domain.shop.model.support.inquiry.enums;

public enum QuestionCategory {
    REGISTRATION(0),
    VERIFICATION(1),
    ORDER(2),
    PAYMENT(3),
    DELIVERY(4);

    private final Integer value;

    QuestionCategory(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
