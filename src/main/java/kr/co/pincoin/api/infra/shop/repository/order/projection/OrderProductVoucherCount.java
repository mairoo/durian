package kr.co.pincoin.api.infra.shop.repository.order.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderProductVoucherCount {
  private String productCode;
  private Long issuedCount;
}
