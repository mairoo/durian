package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

  private final OrderJpaRepository jpaRepository;

  private final OrderQueryRepository queryRepository;

  private final OrderMapper mapper;

  @Override
  public Order save(Order order) {
    return mapper.toModel(jpaRepository.save(mapper.toEntity(order)));
  }

  @Override
  public List<Order> saveAll(Collection<Order> orders) {
    return mapper.toModelList(jpaRepository.saveAll(mapper.toEntityList(orders.stream().toList())));
  }

  @Override
  public Order saveAndFlush(Order order) {
    return mapper.toModel(jpaRepository.save(mapper.toEntity(order)));
  }

  @Override
  public Optional<Order> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toModel);
  }

  @Override
  public Optional<Order> findByIdWithUser(Long orderId) {
    return jpaRepository.findByIdWithUser(orderId).map(mapper::toModel);
  }

  @Override
  public Optional<Order> findByIdAndUserId(Long orderId, Integer userId) {
    return jpaRepository.findByIdAndUserId(orderId, userId).map(mapper::toModel);
  }

  @Override
  public Optional<Order> findByOrderNoAndUserId(String orderNo, Integer userId) {
    return jpaRepository.findByOrderNoAndUserId(orderNo, userId).map(mapper::toModel);
  }

  @Override
  public List<Order> findByUserId(Integer userId) {
    return jpaRepository.findByUserId(userId).stream()
        .map(mapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Order> findByStatus(OrderStatus status) {
    return jpaRepository.findByStatus(status).stream()
        .map(mapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Order> findSuspiciousOrders() {
    return jpaRepository.findSuspiciousOrders().stream()
        .map(mapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable) {
    Page<OrderEntity> orderEntities = queryRepository.searchOrders(condition, pageable);

    List<Order> orders =
        orderEntities.getContent().stream().map(mapper::toModel).collect(Collectors.toList());

    return new PageImpl<>(orders, pageable, orderEntities.getTotalElements());
  }
}
