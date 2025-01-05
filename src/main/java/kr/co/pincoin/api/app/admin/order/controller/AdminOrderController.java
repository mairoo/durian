package kr.co.pincoin.api.app.admin.order.controller;

import kr.co.pincoin.api.app.admin.order.response.AdminOrderResponse;
import kr.co.pincoin.api.app.admin.order.service.AdminOrderService;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.global.response.page.PageResponse;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Slf4j
public class AdminOrderController {
  private final AdminOrderService orderService;

  /**
   * 관리자용 주문 목록을 페이지네이션하여 조회합니다.
   *
   * @param condition 주문 검색 조건 (OrderSearchCondition)
   * @param pageable 페이지네이션 정보 (기본값: 페이지당 20개)
   * @return 주문 목록과 페이지네이션 정보를 포함한 ApiResponse
   */
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<AdminOrderResponse>>> getOrders(
      @ModelAttribute OrderSearchCondition condition,
      @PageableDefault(size = 20) Pageable pageable) {
    Page<Order> orders = orderService.getOrders(condition, pageable);
    Page<AdminOrderResponse> responses = orders.map(AdminOrderResponse::from);

    return ResponseEntity.ok(ApiResponse.of(PageResponse.from(responses)));
  }

  /**
   * 주문 ID에 해당하는 주문의 상세 정보를 조회합니다.
   *
   * @param orderId 조회할 주문의 ID
   * @return 주문 상세 정보를 포함한 ApiResponse
   */
  @GetMapping("/{orderId}")
  public ResponseEntity<ApiResponse<AdminOrderResponse>> getOrder(@PathVariable Long orderId) {
    Order order = orderService.getOrder(orderId);
    return ResponseEntity.ok(ApiResponse.of(AdminOrderResponse.from(order)));
  }

  /**
   * 주문 ID에 해당하는 주문을 삭제 처리합니다.
   *
   * @param orderId 삭제할 주문의 ID
   * @return 삭제 완료 메시지를 포함한 ApiResponse
   */
  @PostMapping("/{orderId}/delete")
  public ResponseEntity<ApiResponse<Void>> deleteMyOrder(@PathVariable Long orderId) {
    orderService.deleteOrder(orderId);
    return ResponseEntity.ok(ApiResponse.of(null, "주문이 삭제되었습니다."));
  }

  /**
   * 주문 ID에 해당하는 주문을 숨김 처리합니다.
   *
   * @param orderId 숨길 주문의 ID
   * @return 숨김 처리 완료 메시지를 포함한 ApiResponse
   */
  @PostMapping("/{orderId}/hide")
  public ResponseEntity<ApiResponse<Void>> hideMyOrder(@PathVariable Long orderId) {
    orderService.hideOrder(orderId);
    return ResponseEntity.ok(ApiResponse.of(null, "주문이 숨김 처리되었습니다."));
  }

  /**
   * 주문에 대한 결제를 처리합니다.
   *
   * @param orderId 결제 처리할 주문의 ID
   * @return 결제 처리 완료 메시지를 포함한 ApiResponse
   */
  // processOrderPayment()

  /**
   * 주문의 결제 내역을 삭제합니다.
   *
   * @param orderId 결제 내역을 삭제할 주문의 ID
   * @return 결제 내역 삭제 완료 메시지를 포함한 ApiResponse
   */
  // deleteOrderPayment()

  /**
   * 주문의 결제 완료 상태로 변경합니다.
   *
   * @param orderId 결제 완료 처리할 주문의 ID
   * @return 결제 완료 처리 메시지를 포함한 ApiResponse
   */
  // completeOrderPayment()

  /**
   * 주문의 결제 완료 상태를 취소합니다.
   *
   * @param orderId 결제 완료를 취소할 주문의 ID
   * @return 결제 완료 취소 메시지를 포함한 ApiResponse
   */
  // cancelOrderPaymentCompletion()

  /**
   * 상품권을 발송 처리하고 재고를 차감합니다.
   *
   * @param orderId 상품권을 발송할 주문의 ID
   * @return 상품권 발송 처리 완료 메시지를 포함한 ApiResponse
   */
  // sendGiftCard()

  /**
   * 주문에 대한 환불을 처리합니다.
   *
   * @param orderId 환불 처리할 주문의 ID
   * @return 환불 처리 완료 메시지를 포함한 ApiResponse
   */
  // processRefund()
}
