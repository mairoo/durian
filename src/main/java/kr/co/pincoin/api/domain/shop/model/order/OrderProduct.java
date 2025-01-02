package kr.co.pincoin.api.domain.shop.model.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class OrderProduct {
  // 핵심 식별 정보 (불변)
  private final Long id;
  private final String name;
  private final String subtitle;
  private final String code;
  private final BigDecimal listPrice;
  private final BigDecimal sellingPrice;
  private final Integer quantity;

  // 생성/수정 시간 (불변)
  private final LocalDateTime created;
  private final LocalDateTime modified;

  // 연관 관계 (불변)
  private final Order order;

  // 상태 정보 (가변)
  private Boolean isRemoved;

  @Builder
  private OrderProduct(
      Long id,
      String name,
      String subtitle,
      String code,
      BigDecimal listPrice,
      BigDecimal sellingPrice,
      Integer quantity,
      LocalDateTime created,
      LocalDateTime modified,
      @Nullable Order order,
      Boolean isRemoved) {
    this.id = id;
    this.name = name;
    this.subtitle = subtitle;
    this.code = code;
    this.listPrice = listPrice;
    this.sellingPrice = sellingPrice;
    this.quantity = quantity;
    this.created = created;
    this.modified = modified;
    this.order = order;
    this.isRemoved = isRemoved;

    validatePrices();
    validateQuantity();
  }

  public static OrderProduct of(
      String name,
      String subtitle,
      String code,
      BigDecimal listPrice,
      BigDecimal sellingPrice,
      Integer quantity,
      Order order) {
    return OrderProduct.builder()
        .name(name)
        .subtitle(subtitle)
        .code(code)
        .listPrice(listPrice)
        .sellingPrice(sellingPrice)
        .quantity(quantity)
        .order(order)
        .build();
  }

  public OrderProductEntity toEntity() {
    return OrderProductEntity.builder()
        .id(this.getId())
        .name(this.getName())
        .subtitle(this.getSubtitle())
        .code(this.getCode())
        .listPrice(this.getListPrice())
        .sellingPrice(this.getSellingPrice())
        .quantity(this.getQuantity())
        .order(
            Optional.ofNullable(this.order)
                .map(order -> OrderEntity.builder().id(order.getId()).build())
                .orElse(null))
        .build();
  }

  public void softDelete() {
    this.isRemoved = true;
  }

  public void restore() {
    this.isRemoved = false;
  }

  public BigDecimal getTotalListPrice() {
    return this.listPrice.multiply(BigDecimal.valueOf(quantity));
  }

  public BigDecimal getTotalSellingPrice() {
    return this.sellingPrice.multiply(BigDecimal.valueOf(quantity));
  }

  public BigDecimal getTotalDiscountAmount() {
    return getTotalListPrice().subtract(getTotalSellingPrice());
  }

  public double getDiscountRate() {
    if (this.listPrice.compareTo(BigDecimal.ZERO) == 0) {
      return 0.0;
    }
    return getUnitDiscountAmount()
        .divide(this.listPrice, 4, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100))
        .doubleValue();
  }

  public BigDecimal getUnitDiscountAmount() {
    return this.listPrice.subtract(this.sellingPrice);
  }

  private void validatePrices() {
    if (listPrice == null || listPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("List price must be non-negative");
    }
    if (sellingPrice == null || sellingPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Selling price must be non-negative");
    }
    if (sellingPrice.compareTo(listPrice) > 0) {
      throw new IllegalArgumentException("Selling price cannot exceed list price");
    }
  }

  private void validateQuantity() {
    if (quantity == null || quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
  }
}
