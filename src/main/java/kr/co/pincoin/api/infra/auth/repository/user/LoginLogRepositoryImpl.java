package kr.co.pincoin.api.infra.auth.repository.user;

import kr.co.pincoin.api.domain.auth.repository.user.LoginLogRepository;
import kr.co.pincoin.api.infra.auth.mapper.user.LoginLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LoginLogRepositoryImpl implements LoginLogRepository {
  private final LoginLogJpaRepository loginLogJpaRepository;

  private final LoginLogQueryRepository loginLogQueryRepository;

  private final LoginLogMapper loginLogMapper;
}
