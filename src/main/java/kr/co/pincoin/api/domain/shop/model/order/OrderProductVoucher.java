package kr.co.pincoin.api.domain.shop.model.order;

import java.time.LocalDateTime;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductVoucherEntity;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class OrderProductVoucher {
  // 핵심 식별 정보 (불변)
  private final Long id;
  private final String code;

  // 연관 관계 (불변)
  private final OrderProduct orderProduct;
  private final Voucher voucher;

  // 생성/수정 시간 (불변)
  private final LocalDateTime created;
  private final LocalDateTime modified;

  // 상태 정보 (가변)
  private Boolean revoked;
  private String remarks;
  private Boolean isRemoved;

  @Builder
  private OrderProductVoucher(
      Long id,
      String code,
      Boolean revoked,
      String remarks,
      @Nullable OrderProduct orderProduct,
      @Nullable Voucher voucher,
      LocalDateTime created,
      LocalDateTime modified,
      Boolean isRemoved) {
    this.id = id;
    this.code = code;
    this.revoked = revoked;
    this.remarks = remarks;
    this.orderProduct = orderProduct;
    this.voucher = voucher;
    this.created = created;
    this.modified = modified;
    this.isRemoved = isRemoved;

    validateCode();
  }

  public OrderProductVoucherEntity toEntity() {
    return OrderProductVoucherEntity.builder()
        .id(this.getId())
        .code(this.getCode())
        .revoked(this.getRevoked())
        .remarks(this.getRemarks())
        .orderProduct(
            Optional.ofNullable(this.orderProduct)
                .map(orderProduct -> OrderProductEntity.builder().id(orderProduct.getId()).build())
                .orElse(null))
        .voucher(
            Optional.ofNullable(this.voucher)
                .map(voucher -> VoucherEntity.builder().id(voucher.getId()).build())
                .orElse(null))
        .build();
  }

  public static OrderProductVoucher of(String code, OrderProduct orderProduct, Voucher voucher) {
    return OrderProductVoucher.builder()
        .code(code)
        .orderProduct(orderProduct)
        .voucher(voucher)
        .build();
  }

  public void revoke() {
    this.revoked = true;
  }

  public void updateRemarks(String remarks) {
    if (remarks == null || remarks.trim().isEmpty()) {
      throw new IllegalArgumentException("Remarks cannot be empty");
    }
    this.remarks = remarks;
  }

  public void softDelete() {
    if (!this.revoked) {
      throw new IllegalStateException("Cannot remove active voucher. Revoke it first.");
    }
    this.isRemoved = true;
  }

  public void restore() {
    this.isRemoved = false;
  }

  public boolean isActive() {
    return !this.revoked && !this.isRemoved;
  }

  public boolean isRevoked() {
    return this.revoked;
  }

  public boolean belongsToOrder(Long orderId) {
    return this.orderProduct != null
        && this.orderProduct.getOrder() != null
        && this.orderProduct.getOrder().getId().equals(orderId);
  }

  private void validateCode() {
    if (code == null || code.trim().isEmpty()) {
      throw new IllegalArgumentException("Voucher code cannot be empty");
    }
    if (!code.matches("^[A-Za-z0-9-]+$")) {
      throw new IllegalArgumentException("Invalid voucher code format");
    }
  }
}
