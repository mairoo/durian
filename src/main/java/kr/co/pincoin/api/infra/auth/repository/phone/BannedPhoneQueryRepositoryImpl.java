package kr.co.pincoin.api.infra.auth.repository.phone;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BannedPhoneQueryRepositoryImpl implements BannedPhoneQueryRepository {
  private final JPAQueryFactory queryFactory;
}
