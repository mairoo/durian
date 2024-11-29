package kr.co.pincoin.api.domain.auth.repository.user;

import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.user.User;

public interface UserRepository {

  User save(User user);

  void delete(User user);

  Optional<User> findById(Integer id);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
