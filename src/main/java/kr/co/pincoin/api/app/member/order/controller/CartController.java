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
     * - 장바구니 조회 로그인한 사용자의 장바구니 정보를 조회
     * - 장바구니가 없는 경우 빈 장바구니를 생성하여 반환
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@CurrentUser User user) {
        Cart cart = cartService.getCart(user);
        return ResponseEntity.ok(ApiResponse.of(CartResponse.from(cart), "장바구니를 조회했습니다."));
    }

    /**
     * - 장바구니 동기화 클라이언트의 장바구니 상태를 서버와 동기화
     * - 장바구니 데이터는 JSON 문자열 형태로 전달
     */
    @PutMapping("/sync")
    public ResponseEntity<ApiResponse<CartResponse>> syncCart(
        @CurrentUser User user,
        @RequestBody CartSyncRequest request) {
        Cart syncedCart = cartService.syncCartData(user, request.getCartData());
        return ResponseEntity.ok(ApiResponse.of(CartResponse.from(syncedCart), "장바구니가 동기화되었습니다."));
    }

    /**
     * 장바구니 초기화 사용자의 장바구니를 빈 상태로 초기화
     */
    @PostMapping("/clear")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(@CurrentUser User user) {
        Cart clearedCart = cartService.syncCartData(user, "[]");
        return ResponseEntity.ok(ApiResponse.of(CartResponse.from(clearedCart), "장바구니가 초기화되었습니다."));
    }
}