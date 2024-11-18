package kr.co.pincoin.api.infra.auth.repository.user;

import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
}