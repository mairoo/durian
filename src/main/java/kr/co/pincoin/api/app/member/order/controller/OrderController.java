package kr.co.pincoin.api.app.member.order.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import kr.co.pincoin.api.app.member.order.request.CartOrderCreateRequest;
import kr.co.pincoin.api.app.member.order.response.OrderProductResponse;
import kr.co.pincoin.api.app.member.order.response.OrderVoucherResponse;
import kr.co.pincoin.api.app.member.order.service.OrderService;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.global.response.model.OrderResponse;
import kr.co.pincoin.api.global.response.page.PageResponse;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import kr.co.pincoin.api.global.security.annotation.CurrentUser;
import kr.co.pincoin.api.global.utils.ClientUtils;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
  private final OrderService orderService;

  // 주문 하기
  // 재주문 하기
  // 환불 요청하기

  /** 내 주문 목록 조회 */
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
      @CurrentUser User user,
      @ModelAttribute OrderSearchCondition condition,
      @PageableDefault(size = 20) Pageable pageable) {
    Page<Order> orders = orderService.getMyOrders(user, condition, pageable);
    Page<OrderResponse> responses = orders.map(OrderResponse::from);

    return ResponseEntity.ok(ApiResponse.of(PageResponse.from(responses)));
  }

  /** 내 주문 상세 조회 */
  @GetMapping("/{orderNo}")
  public ResponseEntity<ApiResponse<OrderResponse>> getMyOrder(
      @CurrentUser User user, @PathVariable String orderNo) {
    Order order = orderService.getMyOrder(user, orderNo);
    return ResponseEntity.ok(ApiResponse.of(OrderResponse.from(order)));
  }

  @GetMapping("/{orderNo}/items")
  public ResponseEntity<ApiResponse<List<OrderProductResponse>>> getMyOrderProducts(
      @CurrentUser User user, @PathVariable String orderNo
  ) {
    List<OrderProduct> orderProducts = orderService.getMyOrderProducts(user, orderNo);
    List<OrderProductResponse> orderProductResponses = orderProducts.stream()
        .map(OrderProductResponse::from)
        .toList();
    return ResponseEntity.ok(ApiResponse.of(orderProductResponses));
  }

  /** 내 주문 삭제 */
  @PostMapping("/{orderNo}/delete")
  public ResponseEntity<ApiResponse<Void>> deleteMyOrder(
      @CurrentUser User user, @PathVariable String orderNo) {
    orderService.deleteMyOrder(user, orderNo);
    return ResponseEntity.ok(ApiResponse.of(null, "주문이 삭제되었습니다."));
  }

  /** 내 주문 숨김 처리 */
  @PostMapping("/{orderNo}/hide")
  public ResponseEntity<ApiResponse<Void>> hideMyOrder(
      @CurrentUser User user, @PathVariable String orderNo) {
    orderService.hideMyOrder(user, orderNo);
    return ResponseEntity.ok(ApiResponse.of(null, "주문이 숨김 처리되었습니다."));
  }

  /** 신규 주문 */
  @PostMapping
  public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
      @CurrentUser User user,
      @Valid @RequestBody CartOrderCreateRequest request,
      HttpServletRequest servletRequest) {
    ClientUtils.ClientInfo clientInfo = ClientUtils.getClientInfo(servletRequest);

    Order createdOrder = orderService.createOrder(request, user, clientInfo);

    return ResponseEntity.ok(
        ApiResponse.of(OrderResponse.from(createdOrder), "주문이 성공적으로 생성되었습니다."));
  }

  /**
   * 내 주문 상품권 목록
   */
  @GetMapping("/{orderNo}/vouchers")
  public ResponseEntity<ApiResponse<List<OrderVoucherResponse>>> getOrderVouchers(
      @CurrentUser User user,
      @PathVariable String orderNo) {
    List<OrderProductVoucher> orderVouchers = orderService.getMyOrderVouchers(user, orderNo);
    List<OrderVoucherResponse> orderVoucherResponses = orderVouchers.stream()
        .map(OrderVoucherResponse::from)
        .toList();

    return ResponseEntity.ok(ApiResponse.of(orderVoucherResponses));
  }
}
