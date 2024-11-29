package kr.co.pincoin.api.domain.shop.model.order.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
  PAYMENT_PENDING(0),
  PAYMENT_COMPLETED(1),
  UNDER_REVIEW(2),
  PAYMENT_VERIFIED(3),
  SHIPPED(4),
  REFUND_REQUESTED(5),
  REFUND_PENDING(6),
  REFUNDED1(7),
  REFUNDED2(8),
  VOIDED(9);

  private final Integer value;

  OrderStatus(Integer value) {
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }
}
