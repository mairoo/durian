package kr.co.pincoin.api.domain.shop.model.product;

import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
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

    private VoucherStatus status;

    private Boolean isRemoved;

    @Builder
    private Voucher(Long id, String code,
                    String remarks,
                    VoucherStatus status,
                    Product product,
                    LocalDateTime created,
                    LocalDateTime modified,
                    Boolean isRemoved) {
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

    public void
    updateRemarks(String remarks) {
        if (remarks == null || remarks.trim().isEmpty()) {
            throw new IllegalArgumentException("Remarks cannot be empty");
        }
        this.remarks = remarks;
    }

    public void
    restore() {
        this.isRemoved = false;
    }

    public boolean
    belongsToProduct(Long productId) {
        return this.product != null &&
                this.product.getId().equals(productId);
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