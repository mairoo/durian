package kr.co.pincoin.api.app.member.order.controller;

import kr.co.pincoin.api.app.member.order.request.CartSyncRequest;
import kr.co.pincoin.api.app.member.order.response.CartResponse;
import kr.co.pincoin.api.app.member.order.service.CartService;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Cart;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import kr.co.pincoin.api.global.security.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

  private final CartService cartService;

  /**
   * 로그인한 사용자의 장바구니 정보를 조회합니다. 장바구니가 없는 경우 빈 장바구니를 생성하여 반환합니다.
   *
   * @param user 현재 로그인한 사용자
   * @return 사용자의 장바구니 정보를 포함한 ApiResponse
   */
  @GetMapping
  public ResponseEntity<ApiResponse<CartResponse>> getCart(@CurrentUser User user) {
    Cart cart = cartService.getCart(user);
    return ResponseEntity.ok(ApiResponse.of(CartResponse.from(cart), "장바구니를 조회했습니다."));
  }

  /**
   * 클라이언트의 장바구니 상태를 서버와 동기화합니다. 장바구니 데이터는 JSON 문자열 형태로 전달됩니다.
   *
   * @param user 현재 로그인한 사용자
   * @param request 동기화할 장바구니 데이터를 포함한 요청 객체
   * @return 동기화된 장바구니 정보를 포함한 ApiResponse
   */
  @PutMapping("/sync")
  public ResponseEntity<ApiResponse<CartResponse>> syncCart(
      @CurrentUser User user, @RequestBody CartSyncRequest request) {
    Cart syncedCart = cartService.syncCartData(user, request.getCartData());
    return ResponseEntity.ok(ApiResponse.of(CartResponse.from(syncedCart), "장바구니가 동기화되었습니다."));
  }

  /**
   * 사용자의 장바구니를 빈 상태로 초기화합니다.
   *
   * @param user 현재 로그인한 사용자
   * @return 초기화된 장바구니 정보를 포함한 ApiResponse
   */
  @PostMapping("/clear")
  public ResponseEntity<ApiResponse<CartResponse>> clearCart(@CurrentUser User user) {
    Cart clearedCart = cartService.syncCartData(user, "[]");
    return ResponseEntity.ok(ApiResponse.of(CartResponse.from(clearedCart), "장바구니가 초기화되었습니다."));
  }
}
