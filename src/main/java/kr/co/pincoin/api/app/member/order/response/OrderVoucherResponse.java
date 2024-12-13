package kr.co.pincoin.api.app.member.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderVoucherResponse {

    @JsonProperty("name")
    private final String productName;

    @JsonProperty("subtitle")
    private final String productSubtitle;

    @JsonProperty("code")
    private final String code;

    @JsonProperty("remarks")
    private final String remarks;

    @JsonProperty("revoked")
    private Boolean revoked;

    public static OrderVoucherResponse from(OrderProductVoucherProjection projection) {
        return OrderVoucherResponse.builder()
            .productName(projection.getProductName())
            .productSubtitle(projection.getProductSubtitle())
            .code(projection.getVoucherCode())
            .remarks(projection.getRemarks())
            .revoked(projection.getRevoked())
            .build();
    }
}
