package kr.co.pincoin.api.app.admin.order.controller;

import jakarta.validation.Valid;
import java.util.List;
import kr.co.pincoin.api.app.admin.order.request.AdminOrderPaymentRequest;
import kr.co.pincoin.api.app.admin.order.response.AdminOrderPaymentResponse;
import kr.co.pincoin.api.app.admin.order.service.AdminOrderPaymentService;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Slf4j
public class AdminOrderPaymentController {

  private final AdminOrderPaymentService adminOrderPaymentService;

  @GetMapping("/{orderId}/payments")
  public ResponseEntity<ApiResponse<List<AdminOrderPaymentResponse>>> getOrderPayments(
      @PathVariable Long orderId) {
    List<OrderPaymentDetached> orderPayments = adminOrderPaymentService.getOrderPayments(orderId);

    List<AdminOrderPaymentResponse> responses =
        orderPayments.stream().map(AdminOrderPaymentResponse::from).toList();

    return ResponseEntity.ok(ApiResponse.of(responses));
  }

  @PostMapping("/{orderId}/payments")
  public ResponseEntity<ApiResponse<AdminOrderPaymentResponse>> addOrderPayment(
      @PathVariable Long orderId, @Valid @RequestBody AdminOrderPaymentRequest request) {

    OrderPayment payment = request.toEntity();

    // 결제 추가 및 상태 업데이트
    OrderPayment savedPayment = adminOrderPaymentService.addPayment(orderId, payment);

    return ResponseEntity.ok(ApiResponse.of(AdminOrderPaymentResponse.from(savedPayment)));
  }
}
