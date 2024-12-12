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

  private final ProfileJpaRepository jpaRepository;

  private final ProfileQueryRepository queryRepository;

  private final ProfileMapper mapper;

  @Override
  public Profile save(Profile profile) {
    return mapper.toModel(jpaRepository.save(mapper.toEntity(profile)));
  }

  @Override
  public Page<Profile> findAllWithUser(Pageable pageable) {
    return jpaRepository.findAllWithUser(pageable).map(mapper::toModel);
  }

  @Override
  public Optional<Profile> findByUserId(Integer userId) {
    return jpaRepository.findByUserId(userId).map(mapper::toModel);
  }

  @Override
  public Optional<Profile> findByUserIdWithUser(Integer userId) {
    return jpaRepository.findByUserIdWithUser(userId).map(mapper::toModel);
  }

  @Override
  public Optional<Profile> findByUserWithUser(User user) {
    return jpaRepository.findByUserWithUser(user.toEntity()).map(mapper::toModel);
  }

  @Override
  public void delete(Profile profile) {
    jpaRepository.delete(mapper.toEntity(profile));
  }
}
