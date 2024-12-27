package kr.co.pincoin.api.global.response.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherResponse {

  @JsonProperty("id")
  private final Long id;

  @JsonProperty("code")
  private final String code;

  @JsonProperty("remarks")
  private final String remarks;

  @JsonProperty("status")
  private final VoucherStatus status;

  @JsonProperty("productId")
  private final Long productId;

  @JsonProperty("productName")
  private final String productName;

  @JsonProperty("created")
  private final LocalDateTime created;

  @JsonProperty("modified")
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
