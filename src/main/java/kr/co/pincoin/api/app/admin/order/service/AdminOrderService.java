package kr.co.pincoin.api.app.admin.order.service;

import kr.co.pincoin.api.app.member.order.request.OrderCreateRequest;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.service.OrderDomainService;
import kr.co.pincoin.api.global.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderService {
  private final OrderDomainService orderDomainService;

  /**
   * 주문 목록을 페이징하여 조회한다.
   *
   * @param condition 주문 검색 조건
   * @param pageable 페이징 정보
   * @return 조건에 맞는 주문 목록
   */
  public Page<Order> getOrders(OrderSearchCondition condition, Pageable pageable) {
    return orderDomainService.getOrders(condition, pageable);
  }

  /**
   * 특정 주문을 조회한다.
   *
   * @param orderId 조회할 주문 ID
   * @return 조회된 주문 정보
   */
  public Order getOrder(Long orderId) {
    return orderDomainService.getOrder(orderId);
  }

  /**
   * 관리자가 특정 사용자를 위해 신규 주문을 생성한다.
   *
   * @param userId 주문 생성할 사용자 ID
   * @param request 주문 생성 요청 정보
   * @param clientInfo 클라이언트 정보
   * @return 생성된 주문 정보
   */
  public Order createOrder(
      Integer userId, OrderCreateRequest request, ClientUtils.ClientInfo clientInfo) {
    User user = orderDomainService.getUser(userId);
    return orderDomainService.createOrder(user, request, clientInfo);
  }

  /**
   * 관리자가 기존 주문을 기반으로 재주문을 생성한다.
   *
   * @param userId 재주문할 사용자 ID
   * @param orderNo 원본 주문 번호
   * @param clientInfo 클라이언트 정보
   * @return 생성된 재주문 정보
   */
  public Order reorder(Integer userId, String orderNo, ClientUtils.ClientInfo clientInfo) {
    return orderDomainService.createReorder(userId, orderNo, clientInfo);
  }

  /**
   * 주문을 검증 상태로 변경한다.
   *
   * @param orderId 상태를 변경할 주문 ID
   * @throws IllegalStateException 주문 상태 변경이 불가능한 경우
   */
  public void verifyOrder(Long orderId) {
    Order order = orderDomainService.getOrder(orderId);
    orderDomainService.verifyOrder(order);
  }

  /**
   * 검증된 주문을 검토중 상태로 되돌린다.
   *
   * @param orderId 상태를 변경할 주문 ID
   */
  public void unverifyOrder(Long orderId) {
    Order order = orderDomainService.getOrder(orderId);
    orderDomainService.unverifyOrder(order);
  }

  /**
   * 주문에 대한 바우처를 수동으로 발행한다.
   *
   * @param orderId 발권 처리할 주문 ID
   * @return 발권 처리된 주문 정보
   */
  public Order issueVouchers(Long orderId) {
    Order order = orderDomainService.getOrder(orderId);
    return orderDomainService.issueVouchers(order);
  }

  /**
   * 주문에 대한 환불 처리를 완료한다.
   *
   * @param orderId 환불 처리할 주문 ID
   * @return 환불 처리된 주문 정보
   */
  public Order completeRefund(Long orderId) {
    Order order = orderDomainService.getOrder(orderId);
    return orderDomainService.completeRefund(order);
  }

  /**
   * 주문을 논리적으로 삭제 처리한다. (soft delete)
   *
   * @param orderId 삭제할 주문 ID
   */
  public void deleteOrder(Long orderId) {
    orderDomainService.softDeleteOrder(orderId);
  }

  /**
   * 주문을 화면에서 숨김 처리한다.
   *
   * @param orderId 숨길 주문 ID
   */
  public void hideOrder(Long orderId) {
    orderDomainService.hideOrder(orderId);
  }
}
