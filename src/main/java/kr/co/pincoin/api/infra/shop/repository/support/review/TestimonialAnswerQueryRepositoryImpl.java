package kr.co.pincoin.api.infra.shop.repository.support.review;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TestimonialAnswerQueryRepositoryImpl implements TestimonialAnswerQueryRepository {
    private final JPAQueryFactory queryFactory;
}