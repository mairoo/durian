package kr.co.pincoin.api.app.member.order.controller;

import java.util.List;
import kr.co.pincoin.api.app.member.order.response.OrderPaymentResponse;
import kr.co.pincoin.api.app.member.order.service.OrderPaymentService;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import kr.co.pincoin.api.global.security.annotation.CurrentUser;
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
public class OrderPaymentController {
  private final OrderPaymentService orderPaymentService;

  /**
   * 로그인한 사용자의 특정 주문에 대한 결제 내역 목록을 조회합니다.
   *
   * @param user 현재 로그인한 사용자
   * @param orderNo 조회할 주문 번호
   * @return 주문의 결제 내역 목록을 포함한 ApiResponse
   */
  @GetMapping("/{orderNo}/payments")
  public ResponseEntity<ApiResponse<List<OrderPaymentResponse>>> getMyOrderPayments(
      @CurrentUser User user, @PathVariable String orderNo) {
    List<OrderPaymentDetached> orderPayments =
        orderPaymentService.getMyOrderPayments(user, orderNo);

    List<OrderPaymentResponse> responses =
        orderPayments.stream().map(OrderPaymentResponse::from).toList();

    return ResponseEntity.ok(ApiResponse.of(responses));
  }
}
