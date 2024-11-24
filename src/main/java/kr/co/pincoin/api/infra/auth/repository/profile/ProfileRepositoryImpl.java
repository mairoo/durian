package kr.co.pincoin.api.infra.auth.repository.profile;

import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.profile.ProfileRepository;
import kr.co.pincoin.api.infra.auth.mapper.profile.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryImpl implements ProfileRepository {
    private final ProfileJpaRepository profileJpaRepository;

    private final ProfileQueryRepository profileQueryRepository;

    private final ProfileMapper profileMapper;

    @Override
    public Optional<Profile> findByUserWithFetch(User user) {
        return profileJpaRepository.findByUserWithFetch(user)
                .map(profileMapper::toModel);
    }
}