package kr.co.pincoin.api.infra.auth.repository.email;

import kr.co.pincoin.api.domain.auth.model.email.BannedEmail;
import kr.co.pincoin.api.domain.auth.model.email.condition.BannedEmailSearchCondition;
import kr.co.pincoin.api.domain.auth.repository.email.BannedEmailRepository;
import kr.co.pincoin.api.infra.auth.entity.email.BannedEmailEntity;
import kr.co.pincoin.api.infra.auth.mapper.email.BannedEmailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BannedEmailRepositoryImpl implements BannedEmailRepository {
    // 도메인 Repository 구현체
    // JPA + QueryDSL 조합 = 도메인 인터페이스에서 결국 모두 사용 가능

    // JPA, QueryDSL 구현체는 엔티티를 전달하고 엔티티로 반환하는 형태
    // 도메인 모델을 마치 DTO 같이 파라미터 전달하고 엔티티 변환하여 저장하고 받을 때 도메인 모델로 받음

    private final BannedEmailJpaRepository bannedEmailJpaRepository;

    private final BannedEmailQueryRepository bannedEmailQueryRepository;

    private final BannedEmailMapper bannedEmailMapper;

    @Override
    public BannedEmail save(BannedEmail bannedEmail) {
        BannedEmailEntity entity = bannedEmail.toEntity();  // 도메인 모델에 이미 변환 메서드가 있음
        BannedEmailEntity savedEntity = bannedEmailJpaRepository.save(entity);
        return bannedEmailMapper.toModel(savedEntity);
    }

    @Override
    public Optional<BannedEmail> findById(Long id) {
        return bannedEmailJpaRepository.findById(id)
                .map(bannedEmailMapper::toModel);
    }

    @Override
    public Optional<BannedEmail> findByEmail(String email) {
        return bannedEmailJpaRepository.findByEmail(email)
                .map(bannedEmailMapper::toModel);
    }

    @Override
    public List<BannedEmail> findActiveEmails() {
        return bannedEmailJpaRepository.findActiveEmails().stream()
                .map(bannedEmailMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return bannedEmailJpaRepository.existsByEmail(email);
    }

    @Override
    public void delete(BannedEmail bannedEmail) {
        bannedEmailJpaRepository.delete(bannedEmail.toEntity());
    }

    // QueryRepository를 활용한 추가 메서드들
    public List<BannedEmail> findEmailsContainingDomain(String domain) {
        return bannedEmailQueryRepository.findEmailsContainingDomain(domain).stream()
                .map(bannedEmailMapper::toModel)
                .collect(Collectors.toList());
    }

    public List<BannedEmail> searchBannedEmails(BannedEmailSearchCondition condition) {
        return bannedEmailQueryRepository.searchBannedEmails(condition).stream()
                .map(bannedEmailMapper::toModel)
                .collect(Collectors.toList());
    }
}
