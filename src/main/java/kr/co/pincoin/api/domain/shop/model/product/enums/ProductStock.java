package kr.co.pincoin.api.domain.shop.model.product.enums;

import lombok.Getter;

@Getter
public enum ProductStock {
  SOLD_OUT(0),
  IN_STOCK(1);

  private final Integer value;

  ProductStock(Integer value) {
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }
}
