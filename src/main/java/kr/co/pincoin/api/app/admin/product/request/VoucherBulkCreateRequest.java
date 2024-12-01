package kr.co.pincoin.api.app.admin.product.request;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JSON 역직렬화를 위한 기본 생성자
public class VoucherBulkCreateRequest {
  private Long productId;

  private List<VoucherCodeData> voucherData;

  private VoucherBulkCreateRequest(Long productId, List<VoucherCodeData> voucherData) {
    this.productId = productId;
    this.voucherData = voucherData;
  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  public static class VoucherCodeData {
    private String code;

    private String remarks;

    private VoucherCodeData(String code, String remarks) {
      this.code = code;
      this.remarks = remarks;
    }
  }
}
