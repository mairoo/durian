package kr.co.pincoin.api.domain.shop.model.order;

import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductVoucherEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderProductVoucher {
    private final Long id;
    private final String code;
    private final OrderProduct orderProduct;
    private final Voucher voucher;
    private final LocalDateTime created;
    private final LocalDateTime modified;
    private Boolean revoked;
    private String remarks;
    private Boolean isRemoved;

    @Builder
    private OrderProductVoucher(Long id,
                                String code,
                                Boolean revoked,
                                String remarks,
                                OrderProduct orderProduct,
                                Voucher voucher,
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
                .orderProduct(this.getOrderProduct().toEntity())
                .voucher(this.getVoucher().toEntity())
                .build();
    }

    public static OrderProductVoucher of(String code, OrderProduct orderProduct,
                                         Voucher voucher) {
        return OrderProductVoucher.builder()
                .code(code)
                .orderProduct(orderProduct)
                .voucher(voucher)
                .build();
    }

    public void
    revoke() {
        this.revoked = true;
    }

    public void
    updateRemarks(String remarks) {
        if (remarks == null || remarks.trim().isEmpty()) {
            throw new IllegalArgumentException("Remarks cannot be empty");
        }
        this.remarks = remarks;
    }

    public void
    softDelete() {
        if (!this.revoked) {
            throw new IllegalStateException("Cannot remove active voucher. Revoke it first.");
        }
        this.isRemoved = true;
    }

    public void
    restore() {
        this.isRemoved = false;
    }

    public boolean
    isActive() {
        return !this.revoked && !this.isRemoved;
    }

    public boolean
    isRevoked() {
        return this.revoked;
    }

    public boolean
    belongsToOrder(Long orderId) {
        return this.orderProduct != null &&
                this.orderProduct.getOrder() != null &&
                this.orderProduct.getOrder().getId().equals(orderId);
    }

    private void
    validateCode() {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Voucher code cannot be empty");
        }
        if (!code.matches("^[A-Za-z0-9-]+$")) {
            throw new IllegalArgumentException("Invalid voucher code format");
        }
    }
}
