package kr.co.pincoin.api.infra.auth.repository.email;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.auth.model.email.BannedEmail;
import kr.co.pincoin.api.domain.auth.model.email.condition.BannedEmailSearchCondition;
import kr.co.pincoin.api.domain.auth.repository.email.BannedEmailRepository;
import kr.co.pincoin.api.infra.auth.mapper.email.BannedEmailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BannedEmailRepositoryImpl implements BannedEmailRepository {
  // 도메인 Repository 구현체
  // JPA + QueryDSL 조합 = 도메인 인터페이스에서 결국 모두 사용 가능

  // JPA, QueryDSL 구현체는 엔티티를 전달하고 엔티티로 반환하는 형태
  // 도메인 모델을 마치 DTO 같이 파라미터 전달하고 엔티티 변환하여 저장하고 받을 때 도메인 모델로 받음

  private final BannedEmailJpaRepository jpaRepository;

  private final BannedEmailQueryRepository queryRepository;

  private final BannedEmailMapper emailMapper;

  @Override
  public BannedEmail save(BannedEmail bannedEmail) {
    return emailMapper.toModel(jpaRepository.save(bannedEmail.toEntity()));
  }

  @Override
  public Optional<BannedEmail> findById(Long id) {
    return jpaRepository.findById(id).map(emailMapper::toModel);
  }

  @Override
  public Optional<BannedEmail> findByEmail(String email) {
    return jpaRepository.findByEmail(email).map(emailMapper::toModel);
  }

  @Override
  public List<BannedEmail> findAllByActiveTrue() {
    return jpaRepository.findActiveEmails().stream()
        .map(emailMapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(BannedEmail bannedEmail) {
    jpaRepository.delete(bannedEmail.toEntity());
  }

  @Override
  public List<BannedEmail> findByDomainContaining(String domain) {
    return queryRepository.findByDomainContaining(domain).stream()
        .map(emailMapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<BannedEmail> findByEmailLike(String pattern) {
    return queryRepository.findByEmailLike(pattern).stream()
        .map(emailMapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<BannedEmail> searchBannedEmails(BannedEmailSearchCondition condition) {
    return queryRepository.searchBannedEmails(condition).stream()
        .map(emailMapper::toModel)
        .collect(Collectors.toList());
  }
}
