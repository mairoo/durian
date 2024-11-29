package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;

public interface OrderPaymentRepository {
  OrderPayment save(OrderPayment orderPayment);

  Optional<OrderPayment> findById(Long id);

  List<OrderPayment> findByOrderAndIsRemovedFalse(Order order);
}
