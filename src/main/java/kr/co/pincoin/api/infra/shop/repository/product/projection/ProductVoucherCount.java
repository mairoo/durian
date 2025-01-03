package kr.co.pincoin.api.infra.shop.repository.product.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductVoucherCount {
  private String productCode;
  private Long availableCount;
}
