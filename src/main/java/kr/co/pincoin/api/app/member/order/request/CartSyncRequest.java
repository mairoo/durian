package kr.co.pincoin.api.app.member.order.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Cart;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartSyncRequest {

    @NotBlank(message = "장바구니 데이터는 필수입니다.")
    private String cartData;

    // 새로운 장바구니 생성을 위한 정적 팩토리 메서드
    public static CartSyncRequest createEmpty() {
        CartSyncRequest request = new CartSyncRequest();
        request.cartData = "[]";
        return request;
    }

    // 유효성 검사 메서드
    public void validateCartData() {
        if (cartData == null || cartData.isBlank()) {
            throw new IllegalArgumentException("Cart data cannot be empty");
        }
        try {
            new ObjectMapper().readTree(cartData);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON format in cart data", e);
        }
    }

    // Cart 도메인 모델로 변환하는 메서드
    public Cart toCart(User user) {
        return Cart.of(user, this.cartData);
    }
}