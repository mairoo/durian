package kr.co.pincoin.api.domain.shop.service;

import java.util.Set;
import java.util.UUID;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.infra.shop.service.OrderPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderRefundService {
  private final OrderPersistenceService persistenceService;

  private static final Set<OrderStatus> NON_REFUNDABLE_STATUSES =
      Set.of(
          OrderStatus.REFUND_REQUESTED,
          OrderStatus.REFUND_PENDING,
          OrderStatus.REFUNDED1,
          OrderStatus.REFUNDED2);

  /**
   * 주문에 대한 환불을 요청합니다.
   *
   * @param user 환불을 요청하는 사용자
   * @param order 환불할 주문
   * @param message 환불 요청 메시지
   * @return 생성된 환불 주문
   * @throws IllegalStateException 이미 환불 처리되었거나 삭제된 주문인 경우 발생
   */
  @Transactional
  public Order requestRefund(User user, Order order, String message) {
    validateRefundRequest(order);

    order.updateStatus(OrderStatus.REFUND_REQUESTED);
    order.updateMessage(message);

    Order refundOrder = createRefundOrder(order, user, message);
    persistenceService.saveOrders(order, refundOrder);

    return refundOrder;
  }

  /**
   * 환불 처리를 완료합니다.
   *
   * @param refundOrder 환불 처리할 주문
   * @return 완료된 환불 주문
   * @throws IllegalStateException 환불 처리가 불가능한 상태인 경우 발생
   */
  @Transactional
  public Order completeRefund(Order refundOrder) {
    validateRefundCompletion(refundOrder);

    Order originalOrder = refundOrder.getParent();
    originalOrder.updateStatus(OrderStatus.REFUNDED1);
    refundOrder.updateStatus(OrderStatus.REFUNDED2);

    persistenceService.save(originalOrder);
    persistenceService.save(refundOrder);

    return refundOrder;
  }

  /**
   * 주문이 환불 가능한 상태인지 확인합니다.
   *
   * @param order 확인할 주문
   * @return 환불 가능 여부
   */
  public boolean isRefundable(Order order) {
    return !NON_REFUNDABLE_STATUSES.contains(order.getStatus())
        && !Boolean.TRUE.equals(order.getRemoved());
  }

  /**
   * 주문의 환불 처리가 완료되었는지 확인합니다.
   *
   * @param order 확인할 주문
   * @return 환불 완료 여부
   */
  public boolean isRefundCompleted(Order order) {
    return order.getStatus() == OrderStatus.REFUNDED1 || order.getStatus() == OrderStatus.REFUNDED2;
  }

  /**
   * 부분 환불이 가능한지 확인합니다. (향후 구현 예정)
   *
   * @param order 확인할 주문
   * @return 항상 false 반환 (현재 미지원)
   */
  public boolean isPartialRefundable(Order order) {
    return false; // 현재는 부분 환불을 지원하지 않음
  }

  /**
   * 환불 요청의 유효성을 검사합니다.
   *
   * @param order 검사할 주문
   * @throws IllegalStateException 유효성 검사 실패 시 발생
   */
  private void validateRefundRequest(Order order) {
    if (NON_REFUNDABLE_STATUSES.contains(order.getStatus())) {
      throw new IllegalStateException("이미 환불 처리된 주문입니다.");
    }

    if (Boolean.TRUE.equals(order.getRemoved())) {
      throw new IllegalStateException("삭제된 주문은 환불할 수 없습니다.");
    }
  }

  /**
   * 환불 주문을 생성합니다.
   *
   * @param originalOrder 원본 주문
   * @param user 환불 요청 사용자
   * @param message 환불 메시지
   * @return 생성된 환불 주문
   */
  private Order createRefundOrder(Order originalOrder, User user, String message) {
    return Order.builder()
        .orderNo(generateOrderNumber())
        .fullname(originalOrder.getFullname())
        .userAgent(originalOrder.getUserAgent())
        .acceptLanguage(originalOrder.getAcceptLanguage())
        .ipAddress(originalOrder.getIpAddress())
        .totalListPrice(originalOrder.getTotalListPrice())
        .totalSellingPrice(originalOrder.getTotalSellingPrice())
        .currency(OrderCurrency.KRW)
        .parent(originalOrder)
        .user(user)
        .paymentMethod(originalOrder.getPaymentMethod())
        .status(OrderStatus.REFUND_PENDING)
        .visibility(OrderVisibility.VISIBLE)
        .transactionId("")
        .message(message)
        .suspicious(false)
        .removed(false)
        .build();
  }

  /**
   * 환불 완료 처리의 유효성을 검사합니다.
   *
   * @param refundOrder 검사할 환불 주문
   * @throws IllegalStateException 유효성 검사 실패 시 발생
   */
  private void validateRefundCompletion(Order refundOrder) {
    if (refundOrder.getStatus() != OrderStatus.REFUND_PENDING) {
      throw new IllegalStateException("환불 처리 대기 상태의 주문이 아닙니다.");
    }

    Order originalOrder = refundOrder.getParent();
    if (originalOrder == null) {
      throw new IllegalStateException("환불 처리할 원본 주문을 찾을 수 없습니다.");
    }

    if (originalOrder.getStatus() != OrderStatus.REFUND_REQUESTED) {
      throw new IllegalStateException("환불 요청 상태의 주문이 아닙니다.");
    }
  }

  /**
   * 새로운 주문 번호를 생성합니다.
   *
   * @return 생성된 주문 번호
   */
  private String generateOrderNumber() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}
