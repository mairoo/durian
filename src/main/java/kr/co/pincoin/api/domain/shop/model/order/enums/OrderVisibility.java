package kr.co.pincoin.api.domain.shop.model.order.enums;

public enum OrderVisibility {
  HIDDEN(0),
  VISIBLE(1);

  private final Integer value;

  OrderVisibility(Integer value) {
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }
}
