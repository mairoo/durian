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

  /**
   * 새로운 결제내역을 저장합니다.
   *
   * @param payment 저장할 결제내역
   * @return 저장된 결제내역
   */
  @Transactional
  public OrderPayment savePayment(OrderPayment payment) {
    return orderPaymentRepository.save(payment);
  }

  /**
   * 주문에 대한 결제내역을 조회합니다. (삭제되지 않은 결제만)
   *
   * @param order 주문 정보
   * @return 주문의 결제내역 목록
   */
  public List<OrderPayment> findPaymentsByOrder(Order order) {
    return orderPaymentRepository.findByOrderAndIsRemovedFalse(order);
  }

  /**
   * 주문 ID로 결제내역을 조회합니다.
   *
   * @param orderId 주문 ID
   * @return 주문의 결제내역 목록 (분리된 형태)
   */
  public List<OrderPaymentDetached> findOrderPayments(Long orderId) {
    return orderPaymentRepository.findOrderPaymentsDetachedByOrderId(orderId);
  }

  /**
   * 주문의 총 결제금액을 조회합니다.
   *
   * @param order 주문 정보
   * @return 총 결제금액
   */
  public BigDecimal getTotalAmountByOrder(Order order) {
    return orderPaymentRepository.getTotalAmountByOrder(order);
  }
}
