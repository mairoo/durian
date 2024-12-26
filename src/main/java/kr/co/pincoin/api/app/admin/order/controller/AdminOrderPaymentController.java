package kr.co.pincoin.api.app.admin.order.controller;

import java.util.List;
import kr.co.pincoin.api.app.admin.order.service.AdminOrderPaymentService;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Slf4j
public class AdminOrderPaymentController {

  private final AdminOrderPaymentService adminOrderPaymentService;

  @GetMapping("/{orderId}/payments")
  public ResponseEntity<ApiResponse<List<OrderPayment>>> getOrderPayments(
      @PathVariable Long orderId) {
    List<OrderPayment> orderPayments = adminOrderPaymentService.getOrderPayments(orderId);

    return ResponseEntity.ok(ApiResponse.of(orderPayments));
  }
}
