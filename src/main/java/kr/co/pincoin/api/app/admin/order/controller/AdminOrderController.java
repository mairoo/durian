package kr.co.pincoin.api.app.admin.order.controller;

import kr.co.pincoin.api.app.admin.order.response.AdminOrderResponse;
import kr.co.pincoin.api.app.admin.order.service.AdminOrderService;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.global.response.page.PageResponse;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import kr.co.pincoin.api.global.security.annotation.CurrentUser;
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

  // - 주문 결제 처리
  // - 주문 결제 내역 삭제
  // - 주문 결제 완료 처리
  // - 주문 결제 완료 취소
  // - 상품권 발송 처리 (재고 차감, 상품권 상태 변경)
  // - 주문 환불 처리

  /** 주문 목록 조회 */
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<AdminOrderResponse>>> getOrders(
      @CurrentUser User user,
      @ModelAttribute OrderSearchCondition condition,
      @PageableDefault(size = 20) Pageable pageable) {
    Page<Order> orders = orderService.getOrders(condition, pageable);
    Page<AdminOrderResponse> responses = orders.map(AdminOrderResponse::from);

    return ResponseEntity.ok(ApiResponse.of(PageResponse.from(responses)));
  }

  /** 주문 상세 조회 */
  @GetMapping("/{orderId}")
  public ResponseEntity<ApiResponse<AdminOrderResponse>> getOrder(
      @CurrentUser User user, @PathVariable Long orderId) {
    Order order = orderService.getOrder(orderId);
    return ResponseEntity.ok(ApiResponse.of(AdminOrderResponse.from(order)));
  }

  /** 주문 삭제 */
  @PostMapping("/{orderId}/delete")
  public ResponseEntity<ApiResponse<Void>> deleteMyOrder(
      @CurrentUser User user, @PathVariable Long orderId) {
    orderService.deleteOrder(orderId);
    return ResponseEntity.ok(ApiResponse.of(null, "주문이 삭제되었습니다."));
  }

  /** 주문 숨김 처리 */
  @PostMapping("/{orderId}/hide")
  public ResponseEntity<ApiResponse<Void>> hideMyOrder(
      @PathVariable Long orderId, @CurrentUser User user) {
    orderService.hideOrder(orderId);
    return ResponseEntity.ok(ApiResponse.of(null, "주문이 숨김 처리되었습니다."));
  }
}
