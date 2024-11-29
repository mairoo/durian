package kr.co.pincoin.api.domain.shop.model.order.enums;

import lombok.Getter;

@Getter
public enum OrderPaymentMethod {
  BANK_TRANSFER(0),
  ESCROW(1),
  PAYPAL(2),
  CREDIT_CARD(3),
  BANK_TRANSFER_PG(4),
  VIRTUAL_ACCOUNT(5),
  PHONE_BILL(6);

  private final Integer value;

  OrderPaymentMethod(Integer value) {
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }
}
