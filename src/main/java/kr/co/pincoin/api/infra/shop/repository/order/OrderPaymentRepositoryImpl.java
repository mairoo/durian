package kr.co.pincoin.api.infra.shop.repository.order;

import java.math.BigDecimal;
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

  private final OrderPaymentJpaRepository jpaRepository;

  private final OrderPaymentQueryRepository queryRepository;

  private final OrderPaymentMapper mapper;

  @Override
  public OrderPayment save(OrderPayment orderPayment) {
    return mapper.toModel(jpaRepository.save(mapper.toEntity(orderPayment)));
  }

  @Override
  public Optional<OrderPayment> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toModel);
  }

  @Override
  public List<OrderPayment> findByOrderAndIsRemovedFalse(Order order) {
    List<OrderPaymentEntity> entries = jpaRepository.findByOrderAndRemovedFalse(order.toEntity());

    return mapper.toModelList(entries);
  }

  @Override
  public BigDecimal getTotalAmountByOrder(Order order) {
    return jpaRepository.getTotalAmountByOrder(order.toEntity());
  }
}
