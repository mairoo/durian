package kr.co.pincoin.api.domain.shop.model.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class OrderProductDetached {

  // 핵심 식별 정보
  private final Long id;
  private final Long orderId;

  // 상품 정보
  private final String name;
  private final String subtitle;
  private final String code;
  private final BigDecimal listPrice;
  private final BigDecimal sellingPrice;
  private final Integer quantity;

  // 생성/수정 시간
  private final LocalDateTime created;
  private final LocalDateTime modified;

  // 삭제 상태
  private final boolean isRemoved;

  public OrderProductDetached(
      Long id,
      Long orderId,
      String name,
      String subtitle,
      String code,
      BigDecimal listPrice,
      BigDecimal sellingPrice,
      Integer quantity,
      LocalDateTime created,
      LocalDateTime modified,
      boolean isRemoved) {

    this.id = id;
    this.orderId = orderId;
    this.name = name;
    this.subtitle = subtitle;
    this.code = code;
    this.listPrice = listPrice;
    this.sellingPrice = sellingPrice;
    this.quantity = quantity;
    this.created = created;
    this.modified = modified;
    this.isRemoved = isRemoved;
  }
}
