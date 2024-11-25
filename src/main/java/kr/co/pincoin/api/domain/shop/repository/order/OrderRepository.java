package kr.co.pincoin.api.domain.shop.repository.order;

import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Order saveAndFlush(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findByIdAndUserId(Long orderId, Integer userId);

    Optional<Order> findByOrderNoAndUserId(String orderNo, Integer userId);

    List<Order> findByUserId(Integer userId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findSuspiciousOrders();

    void updateOrderStatus(Long orderId, OrderStatus status);

    void updateTransactionId(Long orderId, String transactionId);

    void markAsSuspicious(Long orderId);

    void softDelete(Long orderId);

    Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable);
}