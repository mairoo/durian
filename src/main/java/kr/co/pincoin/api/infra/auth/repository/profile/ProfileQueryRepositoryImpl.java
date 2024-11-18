package kr.co.pincoin.api.infra.auth.repository.profile;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileQueryRepositoryImpl implements ProfileQueryRepository {
    private final JPAQueryFactory queryFactory;
}