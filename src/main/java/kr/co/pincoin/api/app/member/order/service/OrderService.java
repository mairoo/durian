package kr.co.pincoin.api.app.member.order.service;

import java.util.List;
import kr.co.pincoin.api.app.member.order.request.CartOrderCreateRequest;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductDetached;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.service.OrderProcessingService;
import kr.co.pincoin.api.domain.shop.service.OrderRefundService;
import kr.co.pincoin.api.global.security.authorization.context.OrderRequestContext;
import kr.co.pincoin.api.global.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

  private final OrderProcessingService orderProcessingService;

  private final OrderRefundService orderRefundService;

  private final OrderRequestContext orderContext;

  /**
   * 현재 로그인한 사용자의 주문 목록을 페이징하여 조회한다.
   *
   * @param user 현재 로그인한 사용자
   * @param condition 주문 검색 조건
   * @param pageable 페이징 정보
   * @return 사용자의 주문 목록
   */
  @PreAuthorize("isAuthenticated()")
  public Page<Order> getMyOrders(User user, OrderSearchCondition condition, Pageable pageable) {
    OrderSearchCondition finalCondition = (condition != null)
        ? condition.withUserId(user.getId())
        : OrderSearchCondition.ofUserId(user.getId());

    return orderProcessingService.getOrders(finalCondition, pageable);
  }

  /**
   * 현재 로그인한 사용자의 특정 주문을 조회한다.
   *
   * @param user 현재 로그인한 사용자
   * @param orderNo 조회할 주문 번호
   * @return 사용자의 주문 정보
   */
  @PreAuthorize("@orderSecurityRule.hasOrderAccess(#user, #orderNo)")
  public Order getMyOrder(User user, String orderNo) {
    return orderProcessingService.getUserOrder(user.getId(), orderNo);
  }

  /**
   * 현재 로그인한 사용자의 특정 주문에 포함된 상품 목록을 조회한다.
   *
   * @param user    현재 로그인한 사용자
   * @param orderNo 조회할 주문 번호
   * @return 주문에 포함된 상품 목록
   */
  @PreAuthorize("@orderSecurityRule.hasOrderAccess(#user, #orderNo)")
  public List<OrderProductDetached> getMyOrderProducts(User user, String orderNo) {
    return orderProcessingService.getUserOrderProductsDetached(user, orderNo);
  }

  /**
   * 새로운 주문을 생성한다.
   *
   * @param request 주문 생성 요청 정보
   * @param user 주문하는 사용자
   * @param clientInfo 클라이언트 정보
   * @return 생성된 주문 정보
   */
  @PreAuthorize("isAuthenticated()")
  public Order createOrder(
      CartOrderCreateRequest request, User user, ClientUtils.ClientInfo clientInfo) {
    return orderProcessingService.createOrderFromCart(user, request, clientInfo);
  }

  /**
   * 기존 주문을 기반으로 재주문을 생성한다.
   *
   * @param user 주문하는 사용자
   * @param orderNo 원본 주문 번호
   * @param clientInfo 클라이언트 정보
   * @return 생성된 재주문 정보
   */
  @PreAuthorize("@orderSecurityRule.hasOrderAccess(#user, #orderNo)")
  public Order reorder(User user, String orderNo, ClientUtils.ClientInfo clientInfo) {
    return orderProcessingService.createReorder(user.getId(), orderNo, clientInfo);
  }

  /**
   * 주문에 대한 환불을 요청한다.
   *
   * @param user 환불 요청하는 사용자
   * @param message 환불 요청 메시지
   * @param orderNo 환불 요청할 주문 번호
   * @return 환불 요청된 주문 정보
   */
  @PreAuthorize("@orderSecurityRule.hasOrderAccess(#user, #orderNo)")
  public Order requestRefund(User user, String message, String orderNo) {
    Order order = orderProcessingService.getUserOrder(user.getId(), orderNo);
    return orderRefundService.requestRefund(user, order, message);
  }

  /**
   * 주문을 논리적으로 삭제 처리한다.
   *
   * @param user 삭제 요청하는 사용자
   * @param orderNo 삭제할 주문 번호
   */
  @PreAuthorize("@orderSecurityRule.hasOrderAccess(#user, #orderNo)")
  public void deleteMyOrder(User user, String orderNo) {
    orderProcessingService.softDeleteUserOrder(user.getId(), orderNo);
  }

  /**
   * 주문을 화면에서 숨김 처리한다.
   *
   * @param user 숨김 처리를 요청하는 사용자
   * @param orderNo 숨길 주문 번호
   */
  @PreAuthorize("@orderSecurityRule.hasOrderAccess(#user, #orderNo)")
  public void hideMyOrder(User user, String orderNo) {
    orderProcessingService.hideUserOrder(user.getId(), orderNo);
  }

  /**
   * 현재 로그인한 사용자의 특정 주문에 포함된 상품권 목록을 조회한다.
   *
   * @param user    현재 로그인한 사용자
   * @param orderNo 조회할 주문 번호
   * @return 주문에 포함된 상품권 목록
   */
  @PreAuthorize("@orderSecurityRule.hasOrderAccess(#user, #orderNo)")
  public List<OrderProductVoucher> getMyOrderVouchers(User user, String orderNo) {

    log.debug("getMyOrderVouchers: {} {}", orderNo, orderContext.getOrderId());
    return null;
  }
}
