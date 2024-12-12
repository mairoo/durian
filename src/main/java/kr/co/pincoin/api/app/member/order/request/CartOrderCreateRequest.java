package kr.co.pincoin.api.app.member.order.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderPaymentMethod;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartOrderCreateRequest {

    @NotNull(message = "주문 상품 목록은 필수입니다.")
    @NotEmpty(message = "최소 1개 이상의 상품을 주문해야 합니다.")
    @Valid
    private List<CartItem> items;

    @NotNull(message = "결제 방법은 필수입니다.")
    private OrderPaymentMethod paymentMethod;
}