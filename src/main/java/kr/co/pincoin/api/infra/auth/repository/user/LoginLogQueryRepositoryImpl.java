package kr.co.pincoin.api.infra.auth.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LoginLogQueryRepositoryImpl implements LoginLogQueryRepository {
    private final JPAQueryFactory queryFactory;
}