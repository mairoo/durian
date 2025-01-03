package kr.co.pincoin.api.infra.shop.repository.order;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductDetached;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderProductSearchCondition;
import kr.co.pincoin.api.infra.auth.entity.profile.QProfileEntity;
import kr.co.pincoin.api.infra.auth.entity.user.QUserEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import kr.co.pincoin.api.infra.shop.entity.order.QOrderEntity;
import kr.co.pincoin.api.infra.shop.entity.order.QOrderProductEntity;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductQueryRepositoryImpl implements OrderProductQueryRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<OrderProductEntity> findAll(OrderProductSearchCondition condition) {
    var query = queryFactory.selectFrom(QOrderProductEntity.orderProductEntity);

    if (condition.hasOrderId() || condition.hasOrderNo()) {
      query.innerJoin(QOrderProductEntity.orderProductEntity.order, QOrderEntity.orderEntity);
    }

    if (condition.hasUserId()) {
      query.innerJoin(QOrderEntity.orderEntity.user, QUserEntity.userEntity);
    }

    return query
        .where(
            orderIdEq(condition.getOrderId()),
            orderNoEq(condition.getOrderNo()),
            userIdEq(condition.getUserId()))
        .fetch();
  }

  @Override
  public List<OrderProductDetached> findAllDetached(OrderProductSearchCondition condition) {
    return queryFactory
        .select(getOrderProductDetachedProjection())
        .from(QOrderProductEntity.orderProductEntity)
        .innerJoin(QOrderProductEntity.orderProductEntity.order, QOrderEntity.orderEntity)
        .where(orderIdEq(condition.getOrderId()), orderNoEq(condition.getOrderNo()))
        .fetch();
  }

  private Expression<OrderProductDetached> getOrderProductDetachedProjection() {
    return Projections.constructor(
        OrderProductDetached.class,
        QOrderProductEntity.orderProductEntity.id,
        QOrderProductEntity.orderProductEntity.order.id,
        QOrderProductEntity.orderProductEntity.name,
        QOrderProductEntity.orderProductEntity.subtitle,
        QOrderProductEntity.orderProductEntity.code,
        QOrderProductEntity.orderProductEntity.listPrice,
        QOrderProductEntity.orderProductEntity.sellingPrice,
        QOrderProductEntity.orderProductEntity.quantity,
        QOrderProductEntity.orderProductEntity.created,
        QOrderProductEntity.orderProductEntity.modified,
        QOrderProductEntity.orderProductEntity.isRemoved);
  }

  @Override
  public List<OrderProductEntity> findAllWithOrderAndUser(String orderNo, Integer userId) {
    return queryFactory
        .selectFrom(QOrderProductEntity.orderProductEntity)
        .innerJoin(QOrderProductEntity.orderProductEntity.order, QOrderEntity.orderEntity)
        .fetchJoin()
        .where(orderNoEq(orderNo), userIdEq(userId))
        .fetch();
  }

  @Override
  public List<OrderProductEntity> findAllWithOrder(OrderEntity order) {
    return queryFactory
        .selectFrom(QOrderProductEntity.orderProductEntity)
        .innerJoin(QOrderProductEntity.orderProductEntity.order, QOrderEntity.orderEntity)
        .fetchJoin()
        .where(QOrderEntity.orderEntity.eq(order))
        .fetch();
  }

  @Override
  public List<OrderProductProjection> findAllWithOrderUserProfileByOrderId(Long orderId) {
    return queryFactory
        .select(
            Projections.constructor(
                OrderProductProjection.class,
                QOrderProductEntity.orderProductEntity,
                QOrderEntity.orderEntity,
                QUserEntity.userEntity,
                QProfileEntity.profileEntity))
        .from(QOrderProductEntity.orderProductEntity)
        .innerJoin(QOrderProductEntity.orderProductEntity.order, QOrderEntity.orderEntity)
        .fetchJoin()
        .innerJoin(QOrderEntity.orderEntity.user, QUserEntity.userEntity)
        .fetchJoin()
        .innerJoin(QProfileEntity.profileEntity)
        .on(QProfileEntity.profileEntity.user.eq(QUserEntity.userEntity))
        .fetchJoin()
        .where(QOrderEntity.orderEntity.id.eq(orderId))
        .fetch();
  }

  @Override
  public List<OrderProductProjection> findAllWithOrderByOrderId(Long orderId) {
    return queryFactory
        .select(
            Projections.constructor(
                OrderProductProjection.class,
                QOrderProductEntity.orderProductEntity,
                QOrderEntity.orderEntity))
        .from(QOrderProductEntity.orderProductEntity)
        .innerJoin(QOrderProductEntity.orderProductEntity.order, QOrderEntity.orderEntity)
        .fetchJoin()
        .where(QOrderEntity.orderEntity.id.eq(orderId))
        .fetch();
  }

  private BooleanExpression orderIdEq(Long orderId) {
    return orderId != null ? QOrderProductEntity.orderProductEntity.order.id.eq(orderId) : null;
  }

  private BooleanExpression orderNoEq(String orderNo) {
    return orderNo != null
        ? QOrderProductEntity.orderProductEntity.order.orderNo.eq(orderNo)
        : null;
  }

  private BooleanExpression userIdEq(Integer userId) {
    return userId != null ? QOrderProductEntity.orderProductEntity.order.user.id.eq(userId) : null;
  }
}
