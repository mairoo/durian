package kr.co.pincoin.api.infra.auth.repository.phone;

import kr.co.pincoin.api.domain.auth.repository.phone.BannedPhoneRepository;
import kr.co.pincoin.api.infra.auth.mapper.phone.BannedPhoneMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BannedPhoneRepositoryImpl implements BannedPhoneRepository {

  private final BannedPhoneJpaRepository jpaRepository;

  private final BannedPhoneQueryRepository queryRepository;

  private final BannedPhoneMapper mapper;
}
