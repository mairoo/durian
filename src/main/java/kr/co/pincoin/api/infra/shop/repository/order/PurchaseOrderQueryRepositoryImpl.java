package kr.co.pincoin.api.infra.shop.repository.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PurchaseOrderQueryRepositoryImpl implements PurchaseOrderQueryRepository {
    private final JPAQueryFactory queryFactory;
}