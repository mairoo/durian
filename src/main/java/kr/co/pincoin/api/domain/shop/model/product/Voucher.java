package kr.co.pincoin.api.domain.shop.model.product;

import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Voucher {
    // 핵심 식별 정보 (불변)
    private final Long id;
    private final String code;

    // 연관 관계 (불변)
    private final Product product;

    // 생성/수정 시간 (불변)
    private final LocalDateTime created;
    private final LocalDateTime modified;

    // 상태 정보 (가변)
    private String remarks;
    private VoucherStatus status;
    private Boolean isRemoved;

    @Builder
    private Voucher(Long id,
                    String code,
                    String remarks,
                    VoucherStatus status,
                    Product product,
                    LocalDateTime created,
                    LocalDateTime modified,
                    Boolean isRemoved) {
        this.id = id;
        this.code = code;
        this.product = product;
        this.created = created;
        this.modified = modified;

        this.remarks = remarks;
        this.status = status;
        this.isRemoved = isRemoved;

        validateCode();
    }

    // 팩토리 메소드
    public static Voucher of(String code, Product product) {
        return Voucher.builder()
                .code(code)
                .product(product)
                .build();
    }

    // 엔티티 변환 메소드
    public VoucherEntity toEntity() {
        return VoucherEntity.builder()
                .id(this.getId())
                .code(this.getCode())
                .remarks(this.getRemarks())
                .status(this.getStatus())
                .product(this.getProduct().toEntity())
                .build();
    }

    // 비즈니스 검증 메소드
    public boolean belongsToProduct(Long productId) {
        return this.product != null &&
                this.product.getId().equals(productId);
    }

    // 상태 변경 메소드
    public void markAsSold() {
        if (this.status != VoucherStatus.PURCHASED) {
            throw new IllegalStateException("판매 가능한 상태의 상품권만 판매할 수 있습니다.");
        }
        this.status = VoucherStatus.SOLD;
    }

    public void markAsPurchased() {
        this.status = VoucherStatus.PURCHASED;
    }

    public void updateRemarks(String remarks) {
        if (remarks == null || remarks.trim().isEmpty()) {
            throw new IllegalArgumentException("Remarks cannot be empty");
        }
        this.remarks = remarks;
    }

    public void softDelete() {
        this.isRemoved = true;
    }

    public void restore() {
        this.isRemoved = false;
    }

    // 검증 메소드
    private void validateCode() {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Voucher code cannot be empty");
        }
        if (!code.matches("^[A-Za-z0-9-]+$")) {
            throw new IllegalArgumentException("Invalid voucher code format");
        }
    }
}