package kr.co.pincoin.api.domain.auth.repository.user;

import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.user.User;

public interface UserRepository {
  Optional<User> findByEmail(String email);

  Optional<User> findById(Integer id);

  User save(User user);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  void delete(User user);
}
