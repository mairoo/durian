package kr.co.pincoin.api.app.member.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderProductResponse {

    @JsonProperty("id")
    private final Long id;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("subtitle")
    private final String subtitle;

    @JsonProperty("code")
    private final String code;

    @JsonProperty("listPrice")
    private final BigDecimal listPrice;

    @JsonProperty("sellingPrice")
    private final BigDecimal sellingPrice;

    @JsonProperty("quantity")
    private final Integer quantity;

    // 도메인 모델 객체에서 응답 객체 초기화
    public static OrderProductResponse from(OrderProduct orderProduct) {
        return OrderProductResponse.builder()
            .id(orderProduct.getId())
            .name(orderProduct.getName())
            .subtitle(orderProduct.getSubtitle())
            .code(orderProduct.getCode())
            .listPrice(orderProduct.getListPrice())
            .sellingPrice(orderProduct.getSellingPrice())
            .quantity(orderProduct.getQuantity())
            .build();
    }
}