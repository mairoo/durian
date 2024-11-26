package kr.co.pincoin.api.app.admin.product.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JSON 역직렬화를 위한 기본 생성자
public class VoucherCreateRequest {
    private String code;

    private String remarks;

    private Long productId;

    private VoucherCreateRequest(String code,
                                 String remarks,
                                 Long productId) {
        this.code = code;
        this.remarks = remarks;
        this.productId = productId;
    }
}
