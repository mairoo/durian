package kr.co.pincoin.api.domain.shop.model.product.enums;

import lombok.Getter;

@Getter
public enum ProductStatus {
  ENABLED(0),
  DISABLED(1);

  private final Integer value;

  ProductStatus(Integer value) {
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }
}
