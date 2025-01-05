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

  /**
   * 주문 ID에 해당하는 결제 내역 목록을 조회합니다.
   *
   * @param orderId 조회할 주문의 ID
   * @return 주문의 결제 내역 목록을 포함한 ApiResponse
   */
  @GetMapping("/{orderId}/payments")
  public ResponseEntity<ApiResponse<List<AdminOrderPaymentResponse>>> getOrderPayments(
      @PathVariable Long orderId) {
    List<OrderPaymentDetached> orderPayments = adminOrderPaymentService.getOrderPayments(orderId);

    List<AdminOrderPaymentResponse> responses =
        orderPayments.stream().map(AdminOrderPaymentResponse::from).toList();

    return ResponseEntity.ok(ApiResponse.of(responses));
  }

  /**
   * 주문에 새로운 결제 내역을 추가하고 결제 상태를 업데이트합니다.
   *
   * @param orderId 결제를 추가할 주문의 ID
   * @param request 결제 정보를 포함한 요청 객체 (AdminOrderPaymentRequest)
   * @return 저장된 결제 내역 정보를 포함한 ApiResponse
   */
  @PostMapping("/{orderId}/payments")
  public ResponseEntity<ApiResponse<AdminOrderPaymentResponse>> addOrderPayment(
      @PathVariable Long orderId, @Valid @RequestBody AdminOrderPaymentRequest request) {

    OrderPayment payment = request.toEntity();

    // 결제 추가 및 상태 업데이트
    OrderPayment savedPayment = adminOrderPaymentService.addPayment(orderId, payment);

    return ResponseEntity.ok(ApiResponse.of(AdminOrderPaymentResponse.from(savedPayment)));
  }

  /**
   * 특정 결제 내역을 삭제합니다.
   *
   * @param orderId 주문 ID
   * @param paymentId 삭제할 결제 내역의 ID
   * @return 결제 내역 삭제 완료 메시지를 포함한 ApiResponse
   */
  // deleteOrderPayment()

  /**
   * 결제를 완료 상태로 변경합니다.
   *
   * @param orderId 주문 ID
   * @param paymentId 완료 처리할 결제 내역의 ID
   * @return 결제 완료 처리 메시지를 포함한 ApiResponse
   */
  // completeOrderPayment()

  /**
   * 결제 완료 상태를 취소합니다.
   *
   * @param orderId 주문 ID
   * @param paymentId 완료 취소할 결제 내역의 ID
   * @return 결제 완료 취소 메시지를 포함한 ApiResponse
   */
  // cancelOrderPaymentCompletion()
}
