package kr.co.pincoin.api.infra.auth.repository.user;

import kr.co.pincoin.api.domain.auth.model.user.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginLogJpaRepository extends JpaRepository<LoginLog, Long> {
}