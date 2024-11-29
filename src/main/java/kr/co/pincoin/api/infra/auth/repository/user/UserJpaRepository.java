package kr.co.pincoin.api.infra.auth.repository.user;

import java.util.Optional;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {
  Optional<UserEntity> findByUsername(String username);

  Optional<UserEntity> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
