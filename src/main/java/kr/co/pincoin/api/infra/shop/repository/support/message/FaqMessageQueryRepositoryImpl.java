package kr.co.pincoin.api.infra.shop.repository.support.message;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FaqMessageQueryRepositoryImpl implements FaqMessageQueryRepository {
  private final JPAQueryFactory queryFactory;
}
