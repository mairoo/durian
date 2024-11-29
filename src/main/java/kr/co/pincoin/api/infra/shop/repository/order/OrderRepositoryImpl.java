package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
    public Order saveAndFlush(Order order) {
        return orderMapper.toModel(orderJpaRepository.save(orderMapper.toEntity(order)));
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id)
                .map(orderMapper::toModel);
    }

    @Override
    public Optional<Order> findByIdAndUserId(Long orderId, Integer userId) {
        return orderJpaRepository.findByIdAndUserId(orderId, userId)
                .map(orderMapper::toModel);
    }

    @Override
    public Optional<Order> findByOrderNoAndUserId(String orderNo, Integer userId) {
        return orderJpaRepository.findByOrderNoAndUserId(orderNo, userId)
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
    public Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable) {
        Page<OrderEntity> orderEntities = orderQueryRepository.searchOrders(condition, pageable);

        List<Order> orders = orderEntities.getContent().stream()
                .map(orderMapper::toModel)
                .collect(Collectors.toList());

        return new PageImpl<>(orders, pageable, orderEntities.getTotalElements());
    }
}