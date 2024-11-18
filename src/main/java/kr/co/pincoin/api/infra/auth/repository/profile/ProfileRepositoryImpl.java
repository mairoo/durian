package kr.co.pincoin.api.infra.auth.repository.profile;

import kr.co.pincoin.api.domain.auth.repository.profile.ProfileRepository;
import kr.co.pincoin.api.infra.auth.mapper.profile.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryImpl implements ProfileRepository {
    private final ProfileJpaRepository profileJpaRepository;

    private final ProfileQueryRepository profileQueryRepository;

    private final ProfileMapper profileMapper;
}