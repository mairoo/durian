package kr.co.pincoin.api.infra.auth.repository.phone;

import kr.co.pincoin.api.domain.auth.model.phone.BannedPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannedPhoneJpaRepository extends JpaRepository<BannedPhone, Long> {
}
