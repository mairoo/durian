package kr.co.pincoin.api.infra.auth.repository.user;

import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);
}