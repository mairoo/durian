package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;

    private final OrderQueryRepository orderQueryRepository;

    private final OrderMapper orderMapper;

    @Override
    public Order save(Order order) {
        return orderMapper.toModel(orderJpaRepository.save(orderMapper.toEntity(order)));
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id)
                .map(orderMapper::toModel);
    }

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        return orderJpaRepository.findByOrderNo(orderNo)
                .map(orderMapper::toModel);
    }

    @Override
    public List<Order> findByUserId(Integer userId) {
        return orderJpaRepository.findByUserId(userId).stream()
                .map(orderMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orderJpaRepository.findByStatus(status).stream()
                .map(orderMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findSuspiciousOrders() {
        return orderJpaRepository.findSuspiciousOrders().stream()
                .map(orderMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        orderJpaRepository.updateOrderStatus(orderId, status);
    }

    @Override
    public void updateTransactionId(Long orderId, String transactionId) {
        orderJpaRepository.updateTransactionId(orderId, transactionId);
    }

    @Override
    public void markAsSuspicious(Long orderId) {
        orderJpaRepository.markAsSuspicious(orderId);
    }

    @Override
    public void softDelete(Long orderId) {
        orderJpaRepository.softDelete(orderId, LocalDateTime.now());
    }
}