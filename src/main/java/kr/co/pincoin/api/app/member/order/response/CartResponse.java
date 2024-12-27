package kr.co.pincoin.api.app.member.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.pincoin.api.domain.shop.model.order.Cart;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartResponse {

  @JsonProperty("cartData")
  private String cartData;

  public static CartResponse from(Cart cart) {
    return CartResponse.builder().cartData(cart.getCartData()).build();
  }
}
