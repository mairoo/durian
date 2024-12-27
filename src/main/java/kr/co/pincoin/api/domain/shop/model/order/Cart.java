package kr.co.pincoin.api.domain.shop.model.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.infra.shop.entity.order.CartEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Cart {
  private final Long id;
  private final User user;
  private final Long version;

  private String cartData;

  @Builder
  private Cart(Long id, User user, Long version, String cartData) {
    this.id = id;
    this.user = user;
    this.version = version;
    this.cartData = cartData;
  }

  // 정적 팩토리 메서드
  public static Cart of(User user, Long version, String cartData) {
    return Cart.builder().user(user).version(version).cartData(cartData).build();
  }

  public static Cart of(User user, String cartData) {
    return Cart.builder().user(user).cartData(cartData).version(0L).build();
  }

  public static Cart createEmptyCart(User user) {
    return Cart.builder().user(user).version(0L).cartData("[]").build();
  }

  // Entity 변환 메서드
  public CartEntity toEntity() {
    return CartEntity.builder()
        .id(this.getId())
        .user(this.getUser().toEntity())
        .version(this.getVersion())
        .cartData(this.getCartData())
        .build();
  }

  // 1. 상태/속성 변경 메서드
  public Cart updateCartData(String cartData) {
    validateCartData(cartData);
    this.cartData = cartData;
    return this;
  }

  // 2. JSON 검증 메서드
  private void validateCartData(String cartData) {
    if (cartData == null || cartData.isBlank()) {
      throw new IllegalArgumentException("Cart data cannot be empty");
    }
    try {
      new ObjectMapper().readTree(cartData);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Invalid JSON format in cart data", e);
    }
  }

  public boolean hasValidCartData() {
    try {
      validateCartData(this.cartData);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
