package kr.co.pincoin.api.infra.shop.repository.support.message;

import kr.co.pincoin.api.domain.shop.repository.support.message.SmsRepository;
import kr.co.pincoin.api.infra.shop.mapper.support.message.SmsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SmsRepositoryImpl implements SmsRepository {
  private final SmsJpaRepository smsJpaRepository;

  private final SmsQueryRepository smsQueryRepository;

  private final SmsMapper smsMapper;
}
