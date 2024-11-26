package kr.co.pincoin.api.app.admin.product.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherResponse {
    private final Long id;

    private final String code;

    private final String remarks;

    private final VoucherStatus status;

    private final Long productId;

    private final String productName;

    private final LocalDateTime created;

    private final LocalDateTime modified;

    public static VoucherResponse from(Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .remarks(voucher.getRemarks())
                .status(voucher.getStatus())
                .productId(voucher.getProduct().getId())
                .productName(voucher.getProduct().getName())
                .created(voucher.getCreated())
                .modified(voucher.getModified())
                .build();
    }
}