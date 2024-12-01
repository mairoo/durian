package kr.co.pincoin.api.infra.auth.repository.email;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.infra.auth.entity.email.BannedEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BannedEmailJpaRepository extends JpaRepository<BannedEmailEntity, Long> {
  Optional<BannedEmailEntity> findByEmail(String email);

  @Query("SELECT b FROM BannedEmailEntity b WHERE b.isRemoved = false")
  List<BannedEmailEntity> findActiveEmails();
}
