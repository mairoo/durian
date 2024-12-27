package kr.co.pincoin.api.infra.shop.repository.order;

import static kr.co.pincoin.api.infra.shop.entity.order.QOrderEntity.orderEntity;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.order.OrderDetached;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.infra.auth.entity.profile.QProfileEntity;
import kr.co.pincoin.api.infra.auth.entity.user.QUserEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.infra.shop.entity.order.QOrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository {
  private final JPAQueryFactory queryFactory;

  // ID/OrderNo 기반 사용자 조회
  @Override
  public Optional<Integer> findUserIdByOrderId(Long id) {
    Integer userId =
        queryFactory.select(orderEntity.user.id).from(orderEntity).where(idEquals(id)).fetchOne();

    return Optional.ofNullable(userId);
  }

  @Override
  public Optional<Integer> findUserIdByOrderNo(String orderNo) {
    Integer userId =
        queryFactory
            .select(orderEntity.user.id)
            .from(orderEntity)
            .where(orderNoEquals(orderNo))
            .fetchOne();

    return Optional.ofNullable(userId);
  }

  @Override
  public Optional<OrderDetached> findByOrderDetachedNoAndUserId(String orderNo, Integer userId) {
    return Optional.ofNullable(
        queryFactory
            .select(getOrderDetachedProjection())
            .from(orderEntity)
            .where(orderNoEquals(orderNo), userIdEquals(userId))
            .fetchOne());
  }

  // 검색/페이징
  @Override
  public Page<OrderEntity> searchOrders(OrderSearchCondition condition, Pageable pageable) {
    QOrderEntity order = orderEntity;
    QUserEntity user = QUserEntity.userEntity;
    QProfileEntity profile = QProfileEntity.profileEntity;

    // 메인 쿼리
    JPAQuery<OrderEntity> query =
        queryFactory
            .selectFrom(order)
            .leftJoin(order.user, user)
            .leftJoin(profile)
            .on(profile.user.eq(user))
            .where(
                userIdEquals(condition.getUserId()),
                fullnameContains(condition.getFullname()),
                orderNoEquals(condition.getOrderNo()),
                emailContains(condition.getEmail()),
                usernameContains(condition.getUsername()),
                phoneContains(condition.getPhone()),
                statusEquals(condition.getStatus()),
                createdBetween(condition.getStartDate(), condition.getEndDate()));

    // Sort 처리
    for (Sort.Order o : pageable.getSort()) {
      query.orderBy(getOrderSpecifier(o));
    }

    // 페이징 적용
    List<OrderEntity> orders =
        query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

    // 전체 카운트 쿼리: 불필요한 조인 제거
    Long total =
        queryFactory
            .select(order.count())
            .from(order)
            .where(
                fullnameContains(condition.getFullname()),
                orderNoEquals(condition.getOrderNo()),
                emailContains(condition.getEmail()),
                usernameContains(condition.getUsername()),
                phoneContains(condition.getPhone()),
                statusEquals(condition.getStatus()),
                createdBetween(condition.getStartDate(), condition.getEndDate()))
            .fetchOne();

    long count = total != null ? total : 0L;

    return new PageImpl<>(orders, pageable, count);
  }

  private Expression<OrderDetached> getOrderDetachedProjection() {
    return Projections.constructor(
        OrderDetached.class,
        orderEntity.id,
        orderEntity.orderNo,
        orderEntity.fullname,
        orderEntity.userAgent,
        orderEntity.acceptLanguage,
        orderEntity.ipAddress,
        orderEntity.totalListPrice,
        orderEntity.totalSellingPrice,
        orderEntity.currency,
        orderEntity.parent.id,
        orderEntity.user.id,
        orderEntity.created,
        orderEntity.modified,
        orderEntity.paymentMethod,
        orderEntity.status,
        orderEntity.visibility,
        orderEntity.transactionId,
        orderEntity.message,
        orderEntity.suspicious,
        orderEntity.isRemoved);
  }

  // 기본 조건절
  private BooleanExpression idEquals(Long id) {
    return id != null ? orderEntity.id.eq(id) : null;
  }

  private BooleanExpression orderNoEquals(String orderNo) {
    return StringUtils.hasText(orderNo) ? orderEntity.orderNo.eq(orderNo) : null;
  }

  // 사용자 관련 조건절
  private BooleanExpression userIdEquals(Integer userId) {
    return userId != null ? orderEntity.user.id.eq(userId) : null;
  }

  private BooleanExpression fullnameContains(String fullname) {
    return StringUtils.hasText(fullname) ? orderEntity.fullname.containsIgnoreCase(fullname) : null;
  }

  private BooleanExpression emailContains(String email) {
    QUserEntity user = QUserEntity.userEntity;
    return StringUtils.hasText(email) ? user.email.containsIgnoreCase(email) : null;
  }

  private BooleanExpression usernameContains(String username) {
    QUserEntity user = QUserEntity.userEntity;
    return StringUtils.hasText(username) ? user.username.containsIgnoreCase(username) : null;
  }

  private BooleanExpression phoneContains(String phone) {
    QProfileEntity profile = QProfileEntity.profileEntity;
    return StringUtils.hasText(phone) ? profile.phone.containsIgnoreCase(phone) : null;
  }

  // 상태 관련 조건절
  private BooleanExpression statusEquals(OrderStatus status) {
    return status != null ? orderEntity.status.eq(status) : null;
  }

  private BooleanExpression createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
    QOrderEntity order = orderEntity;

    if (startDate != null && endDate != null) {
      return order.created.between(startDate, endDate);
    }
    if (startDate != null) {
      return order.created.goe(startDate);
    }
    if (endDate != null) {
      return order.created.loe(endDate);
    }
    return null;
  }

  // 정렬 처리
  private OrderSpecifier<?> getOrderSpecifier(Sort.Order order) {
    QOrderEntity orderEntity = QOrderEntity.orderEntity;

    com.querydsl.core.types.Order direction =
        order.isAscending()
            ? com.querydsl.core.types.Order.ASC
            : com.querydsl.core.types.Order.DESC;

    return switch (order.getProperty()) {
      case "created" -> new OrderSpecifier<>(direction, orderEntity.created);
      case "totalListPrice" -> new OrderSpecifier<>(direction, orderEntity.totalListPrice);
      case "totalSellingPrice" -> new OrderSpecifier<>(direction, orderEntity.totalSellingPrice);
      case "status" -> new OrderSpecifier<>(direction, orderEntity.status);
      case "orderNo" -> new OrderSpecifier<>(direction, orderEntity.orderNo);
      default -> new OrderSpecifier<>(direction, orderEntity.id);
    };
  }
}
