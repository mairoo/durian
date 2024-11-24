package kr.co.pincoin.api.domain.shop.repository.order;

import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findByOrderNo(String orderNo);

    List<Order> findByUserId(Integer userId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findSuspiciousOrders();

    void updateOrderStatus(Long orderId, OrderStatus status);

    void updateTransactionId(Long orderId, String transactionId);

    void markAsSuspicious(Long orderId);

    void softDelete(Long orderId);
}