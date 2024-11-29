package kr.co.pincoin.api.domain.shop.model.support.message.enums;

public enum FaqCategory {
  REGISTRATION(0),
  VERIFICATION(1),
  ORDER(2),
  PAYMENT(3),
  DELIVERY(4);

  private final Integer value;

  FaqCategory(Integer value) {
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }
}
