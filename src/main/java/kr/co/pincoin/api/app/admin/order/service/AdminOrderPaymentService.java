package kr.co.pincoin.api.app.admin.order.service;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;
import kr.co.pincoin.api.domain.shop.service.OrderPaymentProcessingService;
import kr.co.pincoin.api.global.security.annotation.SuperUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@SuperUser
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminOrderPaymentService {

  private final OrderPaymentProcessingService orderPaymentProcessingService;

  /**
   * 특정 주문에 포함된 입금 내역을 조회합니다.
   *
   * @param orderId 조회할 주문 번호
   * @return 주문에 포함된 입금 내역 목록
   */
  public List<OrderPaymentDetached> getOrderPayments(Long orderId) {
    return orderPaymentProcessingService.getPayments(orderId);
  }

  /**
   * 주문에 새로운 결제 내역을 추가하고 주문 상태를 업데이트합니다. 결제 추가 전 유효성 검사를 수행합니다.
   *
   * @param orderId 결제를 추가할 주문 번호
   * @param payment 추가할 결제 정보
   * @return 저장된 결제 내역
   */
  @Transactional
  public OrderPayment addPayment(Long orderId, OrderPayment payment) {
    // 결제 추가 전 유효성 검사
    orderPaymentProcessingService.validatePayment(payment);

    // 결제 추가 및 주문 상태 업데이트
    return orderPaymentProcessingService.addPayment(orderId, payment);
  }
}
