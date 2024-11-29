package kr.co.pincoin.api.domain.auth.repository.email;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.email.BannedEmail;
import kr.co.pincoin.api.domain.auth.model.email.condition.BannedEmailSearchCondition;

public interface BannedEmailRepository {

  BannedEmail save(BannedEmail bannedEmail);

  Optional<BannedEmail> findById(Long id);

  void delete(BannedEmail bannedEmail);

  Optional<BannedEmail> findByEmail(String email);

  boolean existsByEmail(String email);

  List<BannedEmail> findAllByActiveTrue();

  List<BannedEmail> findByDomainContaining(String domain);

  List<BannedEmail> findByEmailLike(String pattern);

  List<BannedEmail> searchBannedEmails(BannedEmailSearchCondition condition);
}
