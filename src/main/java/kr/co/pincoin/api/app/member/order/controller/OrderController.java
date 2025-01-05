package kr.co.pincoin.api.app.member.order.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import kr.co.pincoin.api.app.member.order.request.CartOrderCreateRequest;
import kr.co.pincoin.api.app.member.order.response.OrderProductResponse;
import kr.co.pincoin.api.app.member.order.response.OrderResponse;
import kr.co.pincoin.api.app.member.order.service.OrderService;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductDetached;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
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

  // 재주문 하기
  // 환불 요청하기

  /**
   * 로그인한 사용자의 주문 목록을 페이지네이션하여 조회합니다.
   *
   * @param user 현재 로그인한 사용자
   * @param condition 주문 검색 조건 (OrderSearchCondition)
   * @param pageable 페이지네이션 정보 (기본값: 페이지당 20개)
   * @return 사용자의 주문 목록과 페이지네이션 정보를 포함한 ApiResponse
   */
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
      @CurrentUser User user,
      @ModelAttribute OrderSearchCondition condition,
      @PageableDefault(size = 20) Pageable pageable) {
    Page<Order> orders = orderService.getMyOrders(user, condition, pageable);
    Page<OrderResponse> responses = orders.map(OrderResponse::from);

    return ResponseEntity.ok(ApiResponse.of(PageResponse.from(responses)));
  }

  /**
   * 로그인한 사용자의 특정 주문 상세 정보를 조회합니다.
   *
   * @param user 현재 로그인한 사용자
   * @param orderNo 조회할 주문 번호
   * @return 주문 상세 정보를 포함한 ApiResponse
   */
  @GetMapping("/{orderNo}")
  public ResponseEntity<ApiResponse<OrderResponse>> getMyOrder(
      @CurrentUser User user, @PathVariable String orderNo) {
    Order order = orderService.getMyOrder(user, orderNo);
    return ResponseEntity.ok(ApiResponse.of(OrderResponse.from(order)));
  }

  /**
   * 로그인한 사용자의 특정 주문에 포함된 상품 목록을 조회합니다.
   *
   * @param user 현재 로그인한 사용자
   * @param orderNo 조회할 주문 번호
   * @return 주문 상품 목록을 포함한 ApiResponse
   */
  @GetMapping("/{orderNo}/items")
  public ResponseEntity<ApiResponse<List<OrderProductResponse>>> getMyOrderProducts(
      @CurrentUser User user, @PathVariable String orderNo) {
    List<OrderProductDetached> orderProducts = orderService.getMyOrderProducts(user, orderNo);
    List<OrderProductResponse> orderProductResponses =
        orderProducts.stream().map(OrderProductResponse::from).toList();
    return ResponseEntity.ok(ApiResponse.of(orderProductResponses));
  }

  /**
   * 로그인한 사용자의 특정 주문을 삭제 처리합니다.
   *
   * @param user 현재 로그인한 사용자
   * @param orderNo 삭제할 주문 번호
   * @return 삭제 완료 메시지를 포함한 ApiResponse
   */
  @PostMapping("/{orderNo}/delete")
  public ResponseEntity<ApiResponse<Void>> deleteMyOrder(
      @CurrentUser User user, @PathVariable String orderNo) {
    orderService.deleteMyOrder(user, orderNo);
    return ResponseEntity.ok(ApiResponse.of(null, "주문이 삭제되었습니다."));
  }

  /**
   * 로그인한 사용자의 특정 주문을 숨김 처리합니다.
   *
   * @param user 현재 로그인한 사용자
   * @param orderNo 숨길 주문 번호
   * @return 숨김 처리 완료 메시지를 포함한 ApiResponse
   */
  @PostMapping("/{orderNo}/hide")
  public ResponseEntity<ApiResponse<Void>> hideMyOrder(
      @CurrentUser User user, @PathVariable String orderNo) {
    orderService.hideMyOrder(user, orderNo);
    return ResponseEntity.ok(ApiResponse.of(null, "주문이 숨김 처리되었습니다."));
  }

  /**
   * 장바구니 상품으로 새로운 주문을 생성합니다. 클라이언트 정보(IP, User Agent 등)를 함께 저장합니다.
   *
   * @param user 현재 로그인한 사용자
   * @param request 주문 생성에 필요한 정보를 담은 요청 객체
   * @param servletRequest 클라이언트 정보를 포함한 HTTP 요청 객체
   * @return 생성된 주문 정보와 완료 메시지를 포함한 ApiResponse
   */
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
}
