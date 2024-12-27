package kr.co.pincoin.api.domain.shop.repository.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;

public interface OrderPaymentRepository {
  OrderPayment save(OrderPayment orderPayment);

  Optional<OrderPayment> findById(Long id);

  List<OrderPayment> findByOrderAndIsRemovedFalse(Order order);

  List<OrderPayment> findByOrderId(Long orderId);

  List<OrderPaymentDetached> findOrderPaymentDetachedByOrderId(Long orderId);

  BigDecimal getTotalAmountByOrder(Order order);
}
