package kr.co.pincoin.api.infra.shop.repository.order;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

  @Override
  public Optional<Integer> findUserIdByOrderId(Long id) {
    QOrderEntity orderEntity = QOrderEntity.orderEntity;

    Integer userId =
        queryFactory.select(orderEntity.user.id).from(orderEntity).where(idEquals(id)).fetchOne();

    return Optional.ofNullable(userId);
  }

  @Override
  public Optional<Integer> findUserIdByOrderNo(String orderNo) {
    QOrderEntity orderEntity = QOrderEntity.orderEntity;

    Integer userId =
        queryFactory
            .select(orderEntity.user.id)
            .from(orderEntity)
            .where(orderNoEquals(orderNo))
            .fetchOne();

    return Optional.ofNullable(userId);
  }

  @Override
  public Optional<OrderEntity> findByOrderNoAndUserId(String orderNo, Integer userId) {
    QOrderEntity orderEntity = QOrderEntity.orderEntity;

    return Optional.ofNullable(
        queryFactory
            .select(orderEntity)
            .from(orderEntity)
            .where(orderNoEquals(orderNo), userIdEquals(userId))
            .fetchOne());
  }

  @Override
  public Page<OrderEntity> searchOrders(OrderSearchCondition condition, Pageable pageable) {
    QOrderEntity orderEntity = QOrderEntity.orderEntity;
    QUserEntity user = QUserEntity.userEntity;
    QProfileEntity profile = QProfileEntity.profileEntity;

    // 메인 쿼리
    JPAQuery<OrderEntity> query =
        queryFactory
            .selectFrom(orderEntity)
            .leftJoin(orderEntity.user, user)
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
            .select(orderEntity.count())
            .from(orderEntity)
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

  private BooleanExpression idEquals(Long id) {
    QOrderEntity orderEntity = QOrderEntity.orderEntity;

    return id != null ? orderEntity.id.eq(id) : null;
  }

  private BooleanExpression orderNoEquals(String orderNo) {
    QOrderEntity orderEntity = QOrderEntity.orderEntity;

    return StringUtils.hasText(orderNo) ? orderEntity.orderNo.eq(orderNo) : null;
  }

  private BooleanExpression userIdEquals(Integer userId) {
    QOrderEntity orderEntity = QOrderEntity.orderEntity;

    return userId != null ? orderEntity.user.id.eq(userId) : null;
  }

  private BooleanExpression fullnameContains(String fullname) {
    QOrderEntity orderEntity = QOrderEntity.orderEntity;

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

  private BooleanExpression statusEquals(OrderStatus status) {
    QOrderEntity orderEntity = QOrderEntity.orderEntity;

    return status != null ? orderEntity.status.eq(status) : null;
  }

  private BooleanExpression createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
    QOrderEntity orderEntity = QOrderEntity.orderEntity;

    if (startDate != null && endDate != null) {
      return orderEntity.created.between(startDate, endDate);
    }
    if (startDate != null) {
      return orderEntity.created.goe(startDate);
    }
    if (endDate != null) {
      return orderEntity.created.loe(endDate);
    }
    return null;
  }

  private OrderSpecifier<?> getOrderSpecifier(Sort.Order sortOrder) {
    QOrderEntity orderEntity = QOrderEntity.orderEntity;

    com.querydsl.core.types.Order direction =
        sortOrder.isAscending()
            ? com.querydsl.core.types.Order.ASC
            : com.querydsl.core.types.Order.DESC;

    return switch (sortOrder.getProperty()) {
      case "created" -> new OrderSpecifier<>(direction, orderEntity.created);
      case "totalListPrice" -> new OrderSpecifier<>(direction, orderEntity.totalListPrice);
      case "totalSellingPrice" -> new OrderSpecifier<>(direction, orderEntity.totalSellingPrice);
      case "status" -> new OrderSpecifier<>(direction, orderEntity.status);
      case "orderNo" -> new OrderSpecifier<>(direction, orderEntity.orderNo);
      default -> new OrderSpecifier<>(direction, orderEntity.id);
    };
  }
}
