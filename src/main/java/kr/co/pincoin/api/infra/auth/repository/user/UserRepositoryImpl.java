package kr.co.pincoin.api.infra.auth.repository.user;

import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository jpaRepository;

  private final UserQueryRepository queryRepository;

  private final UserMapper mapper;

  @Override
  public Optional<User> findByEmail(String email) {
    return jpaRepository.findByEmail(email).map(mapper::toModel);
  }

  @Override
  public Optional<User> findById(Integer id) {
    return jpaRepository.findById(id).map(mapper::toModel);
  }

  @Override
  public User save(User user) {
    return mapper.toModel(jpaRepository.save(user.toEntity()));
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpaRepository.existsByEmail(email);
  }

  @Override
  public boolean existsByUsername(String username) {
    return jpaRepository.existsByUsername(username);
  }

  @Override
  public void delete(User user) {
    jpaRepository.delete(user.toEntity());
  }
}
