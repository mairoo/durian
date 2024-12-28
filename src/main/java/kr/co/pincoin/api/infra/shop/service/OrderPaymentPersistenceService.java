package kr.co.pincoin.api.infra.shop.service;

import java.math.BigDecimal;
import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;
import kr.co.pincoin.api.domain.shop.repository.order.OrderPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderPaymentPersistenceService {

  private final OrderPaymentRepository orderPaymentRepository;

  /** 주문의 결제내역 조회 (삭제되지 않은 결제만) */
  public List<OrderPayment> findPaymentsByOrder(Order order) {
    return orderPaymentRepository.findByOrderAndIsRemovedFalse(order);
  }

  /** 주문 ID로 결제내역 조회 */
  public List<OrderPaymentDetached> findOrderPayments(Long orderId) {
    return orderPaymentRepository.findOrderPaymentDetachedByOrderId(orderId);
  }

  /** 주문의 총 결제금액 조회 */
  public BigDecimal getTotalAmountByOrder(Order order) {
    return orderPaymentRepository.getTotalAmountByOrder(order);
  }

  /** 결제내역 저장 */
  @Transactional
  public OrderPayment savePayment(OrderPayment payment) {
    return orderPaymentRepository.save(payment);
  }
}
