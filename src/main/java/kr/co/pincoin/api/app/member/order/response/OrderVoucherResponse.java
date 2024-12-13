package kr.co.pincoin.api.app.member.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
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
    private final String name;

    @JsonProperty("subtitle")
    private final String subtitle;

    @JsonProperty("code")
    private final String code;

    @JsonProperty("remarks")
    private final String remarks;

    @JsonProperty("revoked")
    private Boolean revoked;

    public static OrderVoucherResponse from(OrderProductVoucher orderProductVoucher) {
        return OrderVoucherResponse.builder()
            .name(orderProductVoucher.getOrderProduct().getName())
            .subtitle(orderProductVoucher.getOrderProduct().getSubtitle())
            .code(orderProductVoucher.getCode())
            .remarks(orderProductVoucher.getRemarks())
            .revoked(orderProductVoucher.getRevoked())
            .build();
    }
}
