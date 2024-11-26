package kr.co.pincoin.api.app.member.order.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderPaymentMethod;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JSON 역직렬화를 위한 기본 생성자
@AllArgsConstructor(access = AccessLevel.PRIVATE)   // @Builder 사용 private 생성자
@Builder
public class OrderCreateRequest {
    @NotNull(message = "주문 상품 목록은 필수입니다.")
    @NotEmpty(message = "최소 1개 이상의 상품을 주문해야 합니다.")
    @Valid  // 중첩된 객체의 유효성 검사를 위해 필요
    private List<OrderLineItem> items;

    @NotNull(message = "결제 방법은 필수입니다.")
    private OrderPaymentMethod paymentMethod;
}