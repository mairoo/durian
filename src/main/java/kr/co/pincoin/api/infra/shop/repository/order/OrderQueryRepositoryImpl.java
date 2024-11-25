package kr.co.pincoin.api.infra.shop.repository.order;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.infra.auth.entity.profile.QProfileEntity;
import kr.co.pincoin.api.infra.auth.entity.user.QUserEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.infra.shop.entity.order.QOrderEntity;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository {
    private final JPAQueryFactory queryFactory;

    private final OrderMapper orderMapper;

    @Override
    public Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable) {
        QOrderEntity order = QOrderEntity.orderEntity;
        QUserEntity user = QUserEntity.userEntity;
        QProfileEntity profile = QProfileEntity.profileEntity;

        // 메인 쿼리
        JPAQuery<OrderEntity> query = queryFactory
                .selectFrom(order)
                .leftJoin(order.user, user)
                .leftJoin(profile)
                .on(profile.user.eq(user))
                .where(
                        fullnameContains(condition.getFullname()),
                        orderNoEquals(condition.getOrderNo()),
                        emailContains(condition.getEmail()),
                        usernameContains(condition.getUsername()),
                        statusEquals(condition.getStatus()),
                        createdBetween(condition.getStartDate(), condition.getEndDate())
                      );

        // 정렬 조건 적용
        for (Sort.Order o : pageable.getSort()) {
            PathBuilder<OrderEntity> pathBuilder = new PathBuilder<>(OrderEntity.class, "orderEntity");
            query.orderBy(new OrderSpecifier<>(
                    o.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                    pathBuilder.getString(o.getProperty())));
        }

        // 페이징 적용
        List<OrderEntity> orders = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리
        Long total = queryFactory
                .select(order.count())
                .from(order)
                .leftJoin(order.user, user)
                .leftJoin(profile)
                .on(profile.user.eq(user))
                .where(
                        fullnameContains(condition.getFullname()),
                        orderNoEquals(condition.getOrderNo()),
                        emailContains(condition.getEmail()),
                        usernameContains(condition.getUsername()),
                        statusEquals(condition.getStatus()),
                        createdBetween(condition.getStartDate(), condition.getEndDate())
                      )
                .fetchOne();

        long count = total != null ? total : 0L;

        // 도메인 객체로 변환
        List<Order> result = orders.stream()
                .map(orderMapper::toModel)
                .collect(Collectors.toList());

        return new PageImpl<>(result, pageable, count);
    }

    // 동적 쿼리 조건 메서드들
    private BooleanExpression fullnameContains(String fullname) {
        QOrderEntity order = QOrderEntity.orderEntity;
        return StringUtils.hasText(fullname) ? order.fullname.containsIgnoreCase(fullname) : null;
    }

    private BooleanExpression orderNoEquals(String orderNo) {
        QOrderEntity order = QOrderEntity.orderEntity;
        return StringUtils.hasText(orderNo) ? order.orderNo.eq(orderNo) : null;
    }

    private BooleanExpression emailContains(String email) {
        QOrderEntity order = QOrderEntity.orderEntity;
        QUserEntity user = QUserEntity.userEntity;
        return StringUtils.hasText(email) ? user.email.containsIgnoreCase(email) : null;
    }

    private BooleanExpression usernameContains(String username) {
        QOrderEntity order = QOrderEntity.orderEntity;
        QUserEntity user = QUserEntity.userEntity;
        return StringUtils.hasText(username) ? user.username.containsIgnoreCase(username) : null;
    }

    private BooleanExpression phoneContains(String phone) {
        QProfileEntity profile = QProfileEntity.profileEntity;
        return StringUtils.hasText(phone) ? profile.phone.containsIgnoreCase(phone) : null;
    }

    private BooleanExpression statusEquals(OrderStatus status) {
        QOrderEntity order = QOrderEntity.orderEntity;
        return status != null ? order.status.eq(status) : null;
    }

    private BooleanExpression createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
        QOrderEntity order = QOrderEntity.orderEntity;

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
}
