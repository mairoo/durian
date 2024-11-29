package kr.co.pincoin.api.infra.shop.repository.store;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreQueryRepositoryImpl implements StoreQueryRepository {
  private final JPAQueryFactory queryFactory;
}
