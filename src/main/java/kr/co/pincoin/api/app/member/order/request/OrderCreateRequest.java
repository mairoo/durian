package kr.co.pincoin.api.app.member.order.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderPaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    @NotNull(message = "주문 상품 목록은 필수입니다.")
    @NotEmpty(message = "최소 1개 이상의 상품을 주문해야 합니다.")
    @Valid  // 중첩된 객체의 유효성 검사를 위해 필요
    private List<OrderLineItem> items;

    @NotNull(message = "결제 방법은 필수입니다.")
    private OrderPaymentMethod paymentMethod;
}