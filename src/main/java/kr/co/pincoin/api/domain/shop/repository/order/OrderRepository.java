package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository {
  Order save(Order order);

  Order saveAndFlush(Order order);

  List<Order> saveAll(Collection<Order> orders);

  Optional<Order> findById(Long id);

  Optional<Order> findByIdWithUser(Long id);

  Optional<Order> findByIdAndUserId(Long orderId, Integer userId);

  Optional<Order> findByOrderNoAndUserId(String orderNo, Integer userId);

  List<Order> findByUserId(Integer userId);

  List<Order> findByStatus(OrderStatus status);

  List<Order> findSuspiciousOrders();

  Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable);
}
