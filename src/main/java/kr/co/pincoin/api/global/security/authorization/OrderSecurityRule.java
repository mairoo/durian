package kr.co.pincoin.api.global.security.authorization;

import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.global.security.authorization.context.OrderRequestContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSecurityRule {
  private final OrderRepository orderRepository;

  private final OrderRequestContext orderContext;

  public boolean hasOrderAccess(User user, String orderNo) {
    Order order =
        orderRepository
            .findByOrderNoAndUserId(orderNo, user.getId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

    if (!order.getUser().getId().equals(user.getId())) {
      throw new BusinessException(ErrorCode.FORBIDDEN);
    }

    orderContext.setOrderId(order.getId());
    return true;
  }

  public boolean hasOrderAccess(User user, Long orderId) {
    Integer userId =
        orderRepository
            .findUserIdById(orderId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    return userId.equals(user.getId());
  }
}
