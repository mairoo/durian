package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.repository.order.OrderPaymentRepository;
import kr.co.pincoin.api.infra.shop.entity.order.OrderPaymentEntity;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderPaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderPaymentRepositoryImpl implements OrderPaymentRepository {
  private final OrderPaymentJpaRepository orderPaymentJpaRepository;

  private final OrderPaymentQueryRepository orderPaymentQueryRepository;

  private final OrderPaymentMapper orderPaymentMapper;

  @Override
  public OrderPayment save(OrderPayment orderPayment) {
    return orderPaymentMapper.toModel(
        orderPaymentJpaRepository.save(orderPaymentMapper.toEntity(orderPayment)));
  }

  @Override
  public Optional<OrderPayment> findById(Long id) {
    return orderPaymentJpaRepository.findById(id).map(orderPaymentMapper::toModel);
  }

  @Override
  public List<OrderPayment> findByOrderAndIsRemovedFalse(Order order) {
    List<OrderPaymentEntity> entries =
        orderPaymentJpaRepository.findByOrderAndRemovedFalse(order.toEntity());

    return orderPaymentMapper.toModelList(entries);
  }
}
