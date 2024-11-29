package kr.co.pincoin.api.domain.auth.model.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BannedEmailTest {

  @Test
  @DisplayName("차단된 이메일 생성시 기본값 검증")
  void createBannedEmail() {
    // Given & When
    BannedEmail bannedEmail = BannedEmail.of("test@example.com");

    // Then
    assertThat(bannedEmail.getEmail()).isEqualTo("test@example.com");
    assertThat(bannedEmail.isRemoved()).isFalse();
    assertThat(bannedEmail.getId()).isNull();
  }

  @Test
  @DisplayName("이메일 차단 해제 및 복원 테스트")
  void softDeleteAndRestore() {
    // Given
    BannedEmail bannedEmail =
        BannedEmail.builder().email("test@example.com").isRemoved(false).build();

    // When
    bannedEmail.softDelete();

    // Then
    assertThat(bannedEmail.isRemoved()).isTrue();
    assertThat(bannedEmail.isActive()).isFalse();

    // When
    bannedEmail.restore();

    // Then
    assertThat(bannedEmail.isRemoved()).isFalse();
    assertThat(bannedEmail.isActive()).isTrue();
  }

  @ParameterizedTest
  @DisplayName("정확한 이메일 매칭 테스트")
  @CsvSource({
    "test@example.com, test@example.com, true",
    "test@example.com, TEST@EXAMPLE.COM, true",
    "test@example.com, other@example.com, false",
    "test@example.com, '', false"
  })
  void matchesExactEmail(String bannedEmail, String targetEmail, boolean expected) {
    // Given
    BannedEmail banned = BannedEmail.of(bannedEmail);

    // When & Then
    assertThat(banned.matches(targetEmail)).isEqualTo(expected);
  }

  @ParameterizedTest
  @DisplayName("와일드카드 도메인 매칭 테스트")
  @CsvSource({
    "*@example.com, test@example.com, true",
    "*@example.com, other@example.com, true",
    "*@example.com, test@different.com, false",
    "*@example.com, test@EXAMPLE.COM, true"
  })
  void matchesWildcardDomain(String bannedEmail, String targetEmail, boolean expected) {
    // Given
    BannedEmail banned = BannedEmail.of(bannedEmail);

    // When & Then
    assertThat(banned.matches(targetEmail)).isEqualTo(expected);
  }

  @Test
  @DisplayName("삭제된 이메일은 매칭되지 않아야 함")
  void removedEmailShouldNotMatch() {
    // Given
    BannedEmail bannedEmail =
        BannedEmail.builder().email("test@example.com").isRemoved(true).build();

    // When & Then
    assertThat(bannedEmail.matches("test@example.com")).isFalse();
    assertThat(bannedEmail.matches("other@example.com")).isFalse();
  }

  @Test
  @DisplayName("Entity 변환 테스트")
  void convertToEntity() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    BannedEmail bannedEmail =
        BannedEmail.builder()
            .id(1L)
            .email("test@example.com")
            .created(now)
            .modified(now)
            .isRemoved(false)
            .build();

    // When
    var entity = bannedEmail.toEntity();

    // Then
    assertThat(entity.getId()).isEqualTo(bannedEmail.getId());
    assertThat(entity.getEmail()).isEqualTo(bannedEmail.getEmail());
  }
}
