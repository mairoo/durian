package kr.co.pincoin.api.infra.auth.repository.email;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import kr.co.pincoin.api.domain.auth.model.email.condition.BannedEmailSearchCondition;
import kr.co.pincoin.api.infra.auth.entity.email.BannedEmailEntity;
import kr.co.pincoin.api.infra.auth.entity.email.QBannedEmailEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BannedEmailQueryRepositoryImpl implements BannedEmailQueryRepository {
  private final JPAQueryFactory queryFactory;

  // QueryDSL 구현체 (복잡한 조회 쿼리 구현)

  @Override
  public List<BannedEmailEntity> findEmailsContainingDomain(String domain) {
    QBannedEmailEntity bannedEmail = QBannedEmailEntity.bannedEmailEntity;

    return queryFactory
        .selectFrom(bannedEmail)
        .where(bannedEmail.isRemoved.eq(false), bannedEmail.email.like("*@" + domain))
        .orderBy(bannedEmail.created.desc())
        .fetch();
  }

  @Override
  public List<BannedEmailEntity> findByEmailPattern(String pattern) {
    QBannedEmailEntity bannedEmail = QBannedEmailEntity.bannedEmailEntity;

    return queryFactory
        .selectFrom(bannedEmail)
        .where(bannedEmail.isRemoved.eq(false), bannedEmail.email.like(pattern))
        .orderBy(bannedEmail.created.desc())
        .fetch();
  }

  @Override
  public List<BannedEmailEntity> searchBannedEmails(BannedEmailSearchCondition condition) {
    QBannedEmailEntity bannedEmail = QBannedEmailEntity.bannedEmailEntity;

    return queryFactory
        .selectFrom(bannedEmail)
        .where(
            isRemovedEq(condition.getIsRemoved()),
            condition.hasEmailPattern() ? emailLike(condition.getEmailPattern()) : null,
            condition.hasDateRange()
                ? createdBetween(condition.getStartDate(), condition.getEndDate())
                : null)
        .orderBy(bannedEmail.created.desc())
        .fetch();
  }

  private BooleanExpression isRemovedEq(Boolean isRemoved) {
    return isRemoved != null ? QBannedEmailEntity.bannedEmailEntity.isRemoved.eq(isRemoved) : null;
  }

  private BooleanExpression emailLike(String pattern) {
    return pattern != null ? QBannedEmailEntity.bannedEmailEntity.email.like(pattern) : null;
  }

  private BooleanExpression createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate == null || endDate == null) return null;
    return QBannedEmailEntity.bannedEmailEntity.created.between(startDate, endDate);
  }
}
