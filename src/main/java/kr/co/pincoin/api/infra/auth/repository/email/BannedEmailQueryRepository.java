package kr.co.pincoin.api.infra.auth.repository.email;

import kr.co.pincoin.api.domain.auth.model.email.condition.BannedEmailSearchCondition;
import kr.co.pincoin.api.infra.auth.entity.email.BannedEmailEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannedEmailQueryRepository {
    // 복잡한 조회 쿼리를 위한 QueryDSL 인터페이스
    List<BannedEmailEntity> findEmailsContainingDomain(String domain);

    List<BannedEmailEntity> findByEmailPattern(String pattern);

    List<BannedEmailEntity> searchBannedEmails(BannedEmailSearchCondition condition);
}
