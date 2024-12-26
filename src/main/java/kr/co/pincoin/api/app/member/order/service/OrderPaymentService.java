package kr.co.pincoin.api.app.member.order.service;

import java.util.List;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.service.OrderProcessingService;
import kr.co.pincoin.api.global.security.authorization.context.OrderRequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentService {

  private final OrderProcessingService orderProcessingService;

  private final OrderRequestContext orderContext;

  /**
   * 현재 로그인한 사용자의 특정 주문에 포함된 입금 내역을 조회한다.
   *
   * @param user    현재 로그인한 사용자
   * @param orderNo 조회할 주문 번호
   * @return 주문에 포함된 입금 내역
   */
  @PreAuthorize("@orderSecurityRule.hasOrderAccess(#user, #orderNo)")
  public List<OrderPayment> getMyOrderPayments(User user, String orderNo) {
    return orderProcessingService.findOrderPayments(orderContext.getOrderId());
  }
}
