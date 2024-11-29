package kr.co.pincoin.api.infra.auth.repository.user;

import kr.co.pincoin.api.infra.auth.entity.user.LoginLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginLogJpaRepository extends JpaRepository<LoginLogEntity, Long> {}
