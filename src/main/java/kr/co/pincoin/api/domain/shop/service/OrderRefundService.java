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
  private final OrderVoucherService orderVoucherService;

  private static final Set<OrderStatus> NON_REFUNDABLE_STATUSES =
      Set.of(
          OrderStatus.REFUND_REQUESTED,
          OrderStatus.REFUND_PENDING,
          OrderStatus.REFUNDED1,
          OrderStatus.REFUNDED2);

  /** 환불 요청을 생성한다. */
  @Transactional
  public Order requestRefund(User user, Order order, String message) {
    validateRefundRequest(order);

    // 원본 주문 상태 업데이트
    order.updateStatus(OrderStatus.REFUND_REQUESTED);
    order.updateMessage(message);

    // 환불 주문 생성
    Order refundOrder = createRefundOrder(order, user, message);

    // 원본 주문과 환불 주문을 저장
    persistenceService.saveOrders(order, refundOrder);

    // 발행된 바우처 취소 처리
    //orderVoucherService.revokeVouchers(order.getId());

    return refundOrder;
  }

  /** 환불을 완료 처리한다. */
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

  /** 환불 가능 여부를 확인한다. */
  public boolean isRefundable(Order order) {
    return !NON_REFUNDABLE_STATUSES.contains(order.getStatus())
        && !Boolean.TRUE.equals(order.getRemoved());
  }

  private void validateRefundRequest(Order order) {
    if (NON_REFUNDABLE_STATUSES.contains(order.getStatus())) {
      throw new IllegalStateException("이미 환불 처리된 주문입니다.");
    }

    if (Boolean.TRUE.equals(order.getRemoved())) {
      throw new IllegalStateException("삭제된 주문은 환불할 수 없습니다.");
    }
  }

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

  private String generateOrderNumber() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  /** 환불 처리 상태를 확인한다. */
  public boolean isRefundCompleted(Order order) {
    return order.getStatus() == OrderStatus.REFUNDED1 || order.getStatus() == OrderStatus.REFUNDED2;
  }

  /** 부분 환불이 가능한지 확인한다. (향후 부분 환불 기능 구현시 사용) */
  public boolean isPartialRefundable(Order order) {
    // 현재는 부분 환불을 지원하지 않으므로 항상 false 반환
    return false;
  }
}
