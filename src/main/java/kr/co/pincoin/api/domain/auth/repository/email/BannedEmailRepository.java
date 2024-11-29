package kr.co.pincoin.api.domain.auth.repository.email;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.email.BannedEmail;
import kr.co.pincoin.api.domain.auth.model.email.condition.BannedEmailSearchCondition;

public interface BannedEmailRepository {

  // 기본 CRUD
  BannedEmail save(BannedEmail bannedEmail);

  Optional<BannedEmail> findById(Long id);

  Optional<BannedEmail> findByEmail(String email);

  List<BannedEmail> findActiveEmails();

  boolean existsByEmail(String email);

  void delete(BannedEmail bannedEmail);

  // 도메인 특화 검색 기능
  List<BannedEmail> findEmailsContainingDomain(String domain);

  List<BannedEmail> findByEmailPattern(String pattern);

  List<BannedEmail> searchBannedEmails(BannedEmailSearchCondition condition);
}
