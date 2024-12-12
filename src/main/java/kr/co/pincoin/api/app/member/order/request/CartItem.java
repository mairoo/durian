package kr.co.pincoin.api.app.member.order.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @NotNull(message = "상품명은 필수입니다.")
    private String name;

    private String subtitle;

    @NotNull(message = "상품 코드는 필수입니다.")
    private String code;

    @NotNull(message = "정가 필수입니다.")
    private BigDecimal listPrice;

    @NotNull(message = "판매가 필수입니다.")
    private BigDecimal sellingPrice;

    @NotNull(message = "주문 수량은 필수입니다.")
    @Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다.")
    private Integer quantity;

    public static CartItem from(OrderProduct orderProduct) {
        return CartItem.builder()
            .productId(orderProduct.getId())
            .name(orderProduct.getName())
            .subtitle(orderProduct.getSubtitle())
            .code(orderProduct.getCode())
            .listPrice(orderProduct.getListPrice())
            .sellingPrice(orderProduct.getSellingPrice())
            .quantity(orderProduct.getQuantity())
            .build();
    }
}