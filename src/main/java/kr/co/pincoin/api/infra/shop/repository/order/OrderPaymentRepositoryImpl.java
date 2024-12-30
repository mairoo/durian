package kr.co.pincoin.api.infra.shop.repository.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;
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

  /**
   * 주문 결제 정보를 생성하거나 수정합니다
   *
   * @param orderPayment 저장할 주문 결제 정보
   * @return 저장된 주문 결제 정보
   */
  @Override
  public OrderPayment save(OrderPayment orderPayment) {
    return mapper.toModel(jpaRepository.save(mapper.toEntity(orderPayment)));
  }

  /**
   * ID로 주문 결제 정보를 조회합니다
   *
   * @param id 주문 결제 ID
   * @return 조회된 주문 결제 정보 (없을 경우 Optional.empty)
   */
  @Override
  public Optional<OrderPayment> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toModel);
  }

  /**
   * 주문에 대한 삭제되지 않은 결제 정보 목록을 조회합니다
   *
   * @param order 주문 정보
   * @return 해당 주문의 유효한 결제 정보 목록
   */
  @Override
  public List<OrderPayment> findByOrderAndIsRemovedFalse(Order order) {
    List<OrderPaymentEntity> entries = jpaRepository.findByOrderAndRemovedFalse(order.toEntity());
    return mapper.toModelList(entries);
  }

  /**
   * 주문 ID로 결제 정보 목록을 조회합니다
   *
   * @param orderId 주문 ID
   * @return 해당 주문의 모든 결제 정보 목록
   */
  @Override
  public List<OrderPayment> findByOrderId(Long orderId) {
    List<OrderPaymentEntity> entries = jpaRepository.findByOrderId(orderId);
    return mapper.toModelList(entries);
  }

  /**
   * 주문 ID로 분리된 결제 정보 목록을 조회합니다
   *
   * @param orderId 주문 ID
   * @return 해당 주문의 분리된 결제 정보 목록
   */
  @Override
  public List<OrderPaymentDetached> findOrderPaymentsDetachedByOrderId(Long orderId) {
    return queryRepository.findOrderPaymentsDetachedByOrderId(orderId);
  }

  /**
   * 주문의 총 결제 금액을 조회합니다
   *
   * @param order 주문 정보
   * @return 해당 주문의 총 결제 금액
   */
  @Override
  public BigDecimal getTotalAmountByOrder(Order order) {
    return jpaRepository.getTotalAmountByOrder(order.toEntity());
  }
}
