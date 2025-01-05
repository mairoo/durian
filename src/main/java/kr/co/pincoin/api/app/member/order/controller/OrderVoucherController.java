package kr.co.pincoin.api.app.member.order.controller;

import java.util.List;
import kr.co.pincoin.api.app.member.order.response.OrderVoucherResponse;
import kr.co.pincoin.api.app.member.order.service.OrderService;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import kr.co.pincoin.api.global.security.annotation.CurrentUser;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderVoucherController {
  private final OrderService orderService;

  /**
   * 로그인한 사용자의 특정 주문에 포함된 상품권 목록을 조회합니다.
   *
   * @param user 현재 로그인한 사용자
   * @param orderNo 조회할 주문 번호
   * @return 주문의 상품권 목록을 포함한 ApiResponse
   */
  @GetMapping("/{orderNo}/vouchers")
  public ResponseEntity<ApiResponse<List<OrderVoucherResponse>>> getOrderVouchers(
      @CurrentUser User user, @PathVariable String orderNo) {
    List<OrderProductVoucherProjection> orderVouchers =
        orderService.getMyOrderVouchers(user, orderNo);
    List<OrderVoucherResponse> orderVoucherResponses =
        orderVouchers.stream().map(OrderVoucherResponse::from).toList();

    return ResponseEntity.ok(ApiResponse.of(orderVoucherResponses));
  }
}
