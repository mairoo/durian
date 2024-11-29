package kr.co.pincoin.api.domain.auth.model.email.condition;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BannedEmailSearchCondition {
  private final Boolean isRemoved;

  private final String emailPattern;

  private final LocalDateTime startDate;

  private final LocalDateTime endDate;

  @Builder // 메서드 이름을 명확히
  private BannedEmailSearchCondition(
      Boolean isRemoved, String emailPattern, LocalDateTime startDate, LocalDateTime endDate) {
    this.isRemoved = isRemoved;
    this.emailPattern = emailPattern;
    this.startDate = startDate;
    this.endDate = endDate;

    validate(); // 생성자에서 유효성 검증
  }

  // 정적 팩토리 메서드
  public static BannedEmailSearchCondition ofPattern(String emailPattern) {
    return BannedEmailSearchCondition.builder().emailPattern(emailPattern).build();
  }

  public static BannedEmailSearchCondition ofPeriod(
      LocalDateTime startDate, LocalDateTime endDate) {
    return BannedEmailSearchCondition.builder().startDate(startDate).endDate(endDate).build();
  }

  private void validate() {
    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("시작일이 종료일보다 늦을 수 없습니다.");
    }
  }

  public boolean hasDateRange() {
    return startDate != null && endDate != null;
  }

  public boolean hasEmailPattern() {
    return emailPattern != null && !emailPattern.isBlank();
  }
}
