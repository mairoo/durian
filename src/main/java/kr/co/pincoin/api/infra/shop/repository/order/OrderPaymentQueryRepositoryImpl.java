package kr.co.pincoin.api.infra.shop.repository.order;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;
import kr.co.pincoin.api.infra.shop.entity.order.QOrderPaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderPaymentQueryRepositoryImpl implements OrderPaymentQueryRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<OrderPaymentDetached> findOrderPaymentsDetachedByOrderId(Long orderId) {
    QOrderPaymentEntity orderPayment = QOrderPaymentEntity.orderPaymentEntity;

    return queryFactory
        .select(
            Projections.constructor(
                OrderPaymentDetached.class,
                orderPayment.id,
                orderPayment.account,
                orderPayment.amount,
                orderPayment.balance,
                orderPayment.received,
                orderPayment.order.id,
                orderPayment.order.orderNo,
                orderPayment.created,
                orderPayment.modified,
                orderPayment.isRemoved))
        .from(orderPayment)
        .join(orderPayment.order)
        .where(orderPayment.order.id.eq(orderId))
        .fetch();
  }
}
