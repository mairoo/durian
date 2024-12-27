package kr.co.pincoin.api.domain.shop.service;

import java.math.BigDecimal;
import java.util.List;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.infra.shop.service.OrderPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderPaymentProcessingService {

  private final OrderPersistenceService persistenceService;

  /** 주문에 대한 결제 내역을 조회한다. */
  public List<OrderPayment> getPayments(Order order) {
    return persistenceService.findPaymentsByOrder(order);
  }

  /** 주문에 대한 결제 내역을 조회한다. */
  public List<OrderPaymentDetached> getPayments(Long orderId) {
    return persistenceService.findOrderPayments(orderId);
  }

  /** 주문에 대한 총 결제 금액을 조회한다. */
  public BigDecimal getTotalPaymentAmount(Order order) {
    return persistenceService.getTotalAmountByOrder(order);
  }

  /** 주문에 새로운 결제를 추가하고, 결제 완료 여부에 따라 주문 상태를 업데이트한다. */
  @Transactional
  public OrderPayment addPayment(Long orderId, OrderPayment payment) {
    Order order = persistenceService.findOrderWithUser(orderId);
    OrderPayment savedPayment = persistenceService.savePayment(payment);

    BigDecimal totalPayments = persistenceService.getTotalAmountByOrder(order);

    if (isPaymentCompleted(totalPayments, order.getTotalSellingPrice())) {
      Profile profile = persistenceService.findProfileByOrderUserId(order.getUser().getId());

      OrderStatus newStatus = determineOrderStatus(profile);
      order.updateStatus(newStatus);

      persistenceService.save(order);
    }

    return savedPayment;
  }

  /** 결제가 완료되었는지 확인한다. */
  private boolean isPaymentCompleted(BigDecimal totalPayments, BigDecimal orderAmount) {
    return totalPayments.compareTo(orderAmount) >= 0;
  }

  /** 프로필 인증 상태에 따라 주문 상태를 결정한다. */
  private OrderStatus determineOrderStatus(Profile profile) {
    return profile.isPhoneVerified() && profile.isDocumentVerified()
        ? OrderStatus.PAYMENT_VERIFIED
        : OrderStatus.UNDER_REVIEW;
  }

  /** 결제 상세 정보를 검증한다. */
  public void validatePayment(OrderPayment payment) {
    if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
    }

    // 결제 방법별 추가 검증 로직이 필요한 경우 여기에 구현
  }

  /** 결제 취소 가능 여부를 확인한다. */
  public boolean isCancellable(OrderPayment payment) {
    // 결제 취소 가능 조건 검사
    // 예: 결제 후 일정 시간 이내, 특정 결제 방법만 취소 가능 등
    return !Boolean.TRUE.equals(payment.getIsRemoved())
        && payment.getOrder().getStatus() != OrderStatus.REFUNDED1
        && payment.getOrder().getStatus() != OrderStatus.REFUNDED2;
  }

  /** 주문의 모든 결제가 완료되었는지 확인한다. */
  public boolean isOrderFullyPaid(Order order) {
    BigDecimal totalPayments = getTotalPaymentAmount(order);
    return totalPayments.compareTo(order.getTotalSellingPrice()) >= 0;
  }
}
