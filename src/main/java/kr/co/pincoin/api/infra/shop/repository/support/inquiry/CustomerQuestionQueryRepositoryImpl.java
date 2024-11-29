package kr.co.pincoin.api.infra.shop.repository.support.inquiry;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomerQuestionQueryRepositoryImpl implements CustomerQuestionQueryRepository {
  private final JPAQueryFactory queryFactory;
}
