package kr.co.pincoin.api.infra.auth.repository.profile;

import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.profile.ProfileRepository;
import kr.co.pincoin.api.infra.auth.mapper.profile.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryImpl implements ProfileRepository {
    private final ProfileJpaRepository profileJpaRepository;

    private final ProfileQueryRepository profileQueryRepository;

    private final ProfileMapper profileMapper;

    @Override
    public Profile save(Profile profile) {
        return profileMapper.toModel(profileJpaRepository.save(profileMapper.toEntity(profile)));
    }

    @Override
    public Page<Profile> findAllWithUserFetch(Pageable pageable) {
        return profileJpaRepository.findAllWithUserFetch(pageable)
                .map(profileMapper::toModel);
    }

    @Override
    public Optional<Profile> findByUserIdWithFetch(Integer userId) {
        return profileJpaRepository.findByUserIdWithFetch(userId)
                .map(profileMapper::toModel);
    }

    @Override
    public Optional<Profile> findByUserWithFetch(User user) {
        return profileJpaRepository.findByUserWithFetch(user)
                .map(profileMapper::toModel);
    }

    @Override
    public void delete(Profile profile) {
        profileJpaRepository.delete(profileMapper.toEntity(profile));
    }
}