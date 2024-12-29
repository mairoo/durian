package kr.co.pincoin.api.infra.shop.repository.order;

import static kr.co.pincoin.api.infra.shop.entity.order.QOrderPaymentEntity.orderPaymentEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderPaymentQueryRepositoryImpl implements OrderPaymentQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<OrderPaymentDetached> findOrderPaymentsDetachedByOrderId(Long orderId) {
    return queryFactory
        .select(
            Projections.constructor(
                OrderPaymentDetached.class,
                orderPaymentEntity.id,
                orderPaymentEntity.account,
                orderPaymentEntity.amount,
                orderPaymentEntity.balance,
                orderPaymentEntity.received,
                orderPaymentEntity.order.id,
                orderPaymentEntity.order.orderNo,
                orderPaymentEntity.created,
                orderPaymentEntity.modified,
                orderPaymentEntity.isRemoved))
        .from(orderPaymentEntity)
        .join(orderPaymentEntity.order)
        .where(orderPaymentEntity.order.id.eq(orderId))
        .fetch();
  }
}
