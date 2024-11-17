package kr.co.pincoin.api.domain.shop.model.product;

import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Voucher {
    private final Long id;
    private final String code;
    private final Product product;
    private final LocalDateTime created;
    private final LocalDateTime modified;
    private String remarks;
    private Integer status;
    private Boolean isRemoved;

    @Builder
    private Voucher(Long id, String code, String remarks, Integer status,
                    Product product, LocalDateTime created,
                    LocalDateTime modified, Boolean isRemoved) {
        this.id = id;
        this.code = code;
        this.remarks = remarks;
        this.status = status;
        this.product = product;
        this.created = created;
        this.modified = modified;
        this.isRemoved = isRemoved;

        validateCode();
    }

    public VoucherEntity toEntity() {

        return VoucherEntity.builder()
                .id(this.getId())
                .code(this.getCode())
                .remarks(this.getRemarks())
                .status(this.getStatus())
                .product(this.getProduct().toEntity())
                .build();
    }

    public static Voucher of(String code, Product product) {
        return Voucher.builder()
                .code(code)
                .product(product)
                .build();
    }

    public void markAsUsed() {
        if (!isAvailable()) {
            throw new IllegalStateException("Voucher is not available for use");
        }
        this.status = VoucherStatus.USED.getValue();
    }

    public void markAsExpired() {
        if (isUsed()) {
            throw new IllegalStateException("Cannot expire a used voucher");
        }
        this.status = VoucherStatus.EXPIRED.getValue();
    }

    public void markAsInvalid(String reason) {
        if (isUsed()) {
            throw new IllegalStateException("Cannot invalidate a used voucher");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid reason must be provided");
        }
        this.status = VoucherStatus.INVALID.getValue();
        this.remarks = reason;
    }

    public void updateRemarks(String remarks) {
        if (remarks == null || remarks.trim().isEmpty()) {
            throw new IllegalArgumentException("Remarks cannot be empty");
        }
        this.remarks = remarks;
    }

    public void remove() {
        if (isAvailable()) {
            throw new IllegalStateException("Cannot remove available voucher. Mark it as invalid first.");
        }
        this.isRemoved = true;
    }

    public void restore() {
        this.isRemoved = false;
    }

    public boolean isAvailable() {
        return VoucherStatus.AVAILABLE.getValue().equals(this.status);
    }

    public boolean isUsed() {
        return VoucherStatus.USED.getValue().equals(this.status);
    }

    public boolean isExpired() {
        return VoucherStatus.EXPIRED.getValue().equals(this.status);
    }

    public boolean isInvalid() {
        return VoucherStatus.INVALID.getValue().equals(this.status);
    }

    public boolean belongsToProduct(Long productId) {
        return this.product != null &&
                this.product.getId().equals(productId);
    }

    private void validateCode() {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Voucher code cannot be empty");
        }
        if (!code.matches("^[A-Za-z0-9-]+$")) {
            throw new IllegalArgumentException("Invalid voucher code format");
        }
    }

    public enum VoucherStatus {
        AVAILABLE(1),
        USED(2),
        EXPIRED(3),
        INVALID(4);

        private final Integer value;

        VoucherStatus(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }
}