package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;

public interface OrderProductRepository {
  List<OrderProduct> saveAll(List<OrderProduct> orderProducts);

  List<OrderProduct> findAllByOrderNoAndUserIdWithOrder(String orderNo, Integer userId);

  List<OrderProduct> findAllByOrderWithOrder(Order order);
}
