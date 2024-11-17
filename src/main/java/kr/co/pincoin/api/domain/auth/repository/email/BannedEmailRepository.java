package kr.co.pincoin.api.domain.auth.repository.email;

import kr.co.pincoin.api.domain.auth.model.email.BannedEmail;

import java.util.List;
import java.util.Optional;

public interface BannedEmailRepository {
    // 도메인 중심 repository

    // 기본 CRUD - mapper 사용

    // Spring Data JPA 스타일 기본 쿼리

    // QueryDSL 복잡한 쿼리

    // - 각종 조인

    // - 집계

    // - 페이지네이션

    BannedEmail save(BannedEmail bannedEmail);

    Optional<BannedEmail> findById(Long id);

    Optional<BannedEmail> findByEmail(String email);

    List<BannedEmail> findActiveEmails();

    boolean existsByEmail(String email);

    void delete(BannedEmail bannedEmail);
}
