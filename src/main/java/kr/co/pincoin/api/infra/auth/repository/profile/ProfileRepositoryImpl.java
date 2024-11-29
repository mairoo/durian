package kr.co.pincoin.api.infra.auth.repository.profile;

import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.profile.ProfileRepository;
import kr.co.pincoin.api.infra.auth.mapper.profile.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
  public Page<Profile> findAllWithUser(Pageable pageable) {
    return profileJpaRepository.findAllWithUser(pageable).map(profileMapper::toModel);
  }

  @Override
  public Optional<Profile> findByUserId(Integer userId) {
    return profileJpaRepository.findByUserId(userId).map(profileMapper::toModel);
  }

  @Override
  public Optional<Profile> findByUserIdWithUser(Integer userId) {
    return profileJpaRepository.findByUserIdWithUser(userId).map(profileMapper::toModel);
  }

  @Override
  public Optional<Profile> findByUserWithUser(User user) {
    return profileJpaRepository.findByUserWithUser(user).map(profileMapper::toModel);
  }

  @Override
  public void delete(Profile profile) {
    profileJpaRepository.delete(profileMapper.toEntity(profile));
  }
}
