package kr.co.pincoin.api.infra.shop.repository.order;

import static kr.co.pincoin.api.infra.auth.entity.user.QUserEntity.userEntity;
import static kr.co.pincoin.api.infra.shop.entity.order.QOrderEntity.orderEntity;
import static kr.co.pincoin.api.infra.shop.entity.order.QOrderProductEntity.orderProductEntity;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductDetached;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderProductSearchCondition;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductQueryRepositoryImpl implements OrderProductQueryRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<OrderProductEntity> findAll(OrderProductSearchCondition condition) {
    var query = queryFactory
        .selectFrom(orderProductEntity);

    if (condition.hasOrderId() || condition.hasOrderNo()) {
      query.innerJoin(orderProductEntity.order, orderEntity);
    }

    if (condition.hasUserId()) {
      query.innerJoin(orderEntity.user, userEntity);
    }

    return query
        .where(
            orderIdEq(condition.getOrderId()),
            orderNoEq(condition.getOrderNo()),
            userIdEq(condition.getUserId())
        )
        .fetch();
  }

  @Override
  public List<OrderProductDetached> findAllDetached(OrderProductSearchCondition condition) {
    return queryFactory
        .select(getOrderProductDetachedProjection())
        .from(orderProductEntity)
        .innerJoin(orderProductEntity.order, orderEntity)
        .where(
            orderIdEq(condition.getOrderId()),
            orderNoEq(condition.getOrderNo())
        )
        .fetch();
  }

  private Expression<OrderProductDetached> getOrderProductDetachedProjection() {
    return Projections.constructor(OrderProductDetached.class,
        orderProductEntity.id,
        orderProductEntity.order.id,
        orderProductEntity.name,
        orderProductEntity.subtitle,
        orderProductEntity.code,
        orderProductEntity.listPrice,
        orderProductEntity.sellingPrice,
        orderProductEntity.quantity,
        orderProductEntity.created,
        orderProductEntity.modified,
        orderProductEntity.isRemoved
    );
  }

  @Override
  public List<OrderProductEntity> findAllWithOrderAndUser(String orderNo, Integer userId) {
    return queryFactory
        .selectFrom(orderProductEntity)
        .innerJoin(orderProductEntity.order, orderEntity).fetchJoin()
        .where(
            orderNoEq(orderNo),
            userIdEq(userId)
        )
        .fetch();
  }

  @Override
  public List<OrderProductEntity> findAllWithOrder(OrderEntity order) {
    return queryFactory
        .selectFrom(orderProductEntity)
        .innerJoin(orderProductEntity.order, orderEntity).fetchJoin()
        .where(orderEntity.eq(order))
        .fetch();
  }

  private BooleanExpression orderIdEq(Long orderId) {
    return orderId != null ? orderProductEntity.order.id.eq(orderId) : null;
  }

  private BooleanExpression orderNoEq(String orderNo) {
    return orderNo != null ? orderProductEntity.order.orderNo.eq(orderNo) : null;
  }

  private BooleanExpression userIdEq(Integer userId) {
    return userId != null ? orderProductEntity.order.user.id.eq(userId) : null;
  }
}
