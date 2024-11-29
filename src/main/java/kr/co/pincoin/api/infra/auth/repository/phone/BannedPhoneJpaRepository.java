package kr.co.pincoin.api.infra.auth.repository.phone;

import kr.co.pincoin.api.infra.auth.entity.phone.BannedPhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannedPhoneJpaRepository extends JpaRepository<BannedPhoneEntity, Long> {}
