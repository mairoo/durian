package kr.co.pincoin.api.app.admin.order.service;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.service.OrderProcessingService;
import kr.co.pincoin.api.global.security.annotation.SuperUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderPaymentService {

  private final OrderProcessingService orderProcessingService;

  /**
   * 특정 주문에 포함된 입금 내역을 조회한다.
   *
   * @param orderId 조회할 주문 번호
   * @return 주문에 포함된 입금 내역
   */
  @SuperUser
  public List<OrderPayment> getOrderPayments(Long orderId) {
    return orderProcessingService.findOrderPayments(orderId);
  }
}
