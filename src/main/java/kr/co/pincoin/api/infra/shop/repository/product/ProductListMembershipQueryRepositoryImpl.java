package kr.co.pincoin.api.infra.shop.repository.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductListMembershipQueryRepositoryImpl
    implements ProductListMembershipQueryRepository {
  private final JPAQueryFactory queryFactory;
}
