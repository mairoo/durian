package kr.co.pincoin.api.infra.auth.repository.phone;

import kr.co.pincoin.api.domain.auth.repository.phone.PhoneVerificationLogRepository;
import kr.co.pincoin.api.infra.auth.mapper.phone.PhoneVerificationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PhoneVerificationLogRepositoryImpl implements PhoneVerificationLogRepository {
  private final PhoneVerificationLogJpaRepository phoneVerificationLogJpaRepository;

  private final PhoneVerificationLogQueryRepository phoneVerificationLogQueryRepository;

  private final PhoneVerificationLogMapper phoneVerificationLogMapper;
}
