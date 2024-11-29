package kr.co.pincoin.api.domain.auth.model.phone;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BannedPhoneTest {

    @Test
    @DisplayName("차단된 전화번호 생성시 기본값 검증")
    void createBannedPhone() {
        // Given & When
        BannedPhone bannedPhone = BannedPhone.of("01012345678");

        // Then
        assertThat(bannedPhone)
                .satisfies(phone -> {
                    assertThat(phone.getPhone()).isEqualTo("01012345678");
                    assertThat(phone.isRemoved()).isFalse();
                    assertThat(phone.getId()).isNull();
                    assertThat(phone.getCreated()).isNull();
                    assertThat(phone.getModified()).isNull();
                });
    }

    @Test
    @DisplayName("전화번호 차단 해제 및 복원 테스트")
    void softDeleteAndRestore() {
        // Given
        BannedPhone bannedPhone = BannedPhone.builder()
                .phone("01012345678")
                .isRemoved(false)
                .build();

        // When
        bannedPhone.softDelete();

        // Then
        assertThat(bannedPhone.isRemoved()).isTrue();
        assertThat(bannedPhone.isActive()).isFalse();

        // When
        bannedPhone.restore();

        // Then
        assertThat(bannedPhone.isRemoved()).isFalse();
        assertThat(bannedPhone.isActive()).isTrue();
    }

    @ParameterizedTest
    @DisplayName("다양한 형식의 전화번호 매칭 테스트")
    @CsvSource({
            "01012345678, 010-1234-5678, true",
            "010-1234-5678, 01012345678, true",
            "010.1234.5678, 010-1234-5678, true",
            "010 1234 5678, 01012345678, true",
            "01012345678, 01087654321, false",
            "010-1234-5678, 010-8765-4321, false"
    })
    void matchesPhoneNumber(String bannedPhone, String targetPhone, boolean expected) {
        // Given
        BannedPhone banned = BannedPhone.of(bannedPhone);

        // When & Then
        assertThat(banned.matches(targetPhone)).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("null이나 빈 전화번호는 매칭되지 않아야 함")
    @NullAndEmptySource
    void nullOrEmptyPhonesShouldNotMatch(String targetPhone) {
        // Given
        BannedPhone bannedPhone = BannedPhone.of("01012345678");

        // When & Then
        assertThat(bannedPhone.matches(targetPhone)).isFalse();
    }

    @Test
    @DisplayName("삭제된 전화번호는 매칭되지 않아야 함")
    void removedPhoneShouldNotMatch() {
        // Given
        BannedPhone bannedPhone = BannedPhone.builder()
                .phone("01012345678")
                .isRemoved(true)
                .build();

        // When & Then
        assertThat(bannedPhone.matches("01012345678")).isFalse();
        assertThat(bannedPhone.matches("010-1234-5678")).isFalse();
    }

    @Test
    @DisplayName("Entity 변환 테스트")
    void convertToEntity() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        BannedPhone bannedPhone = BannedPhone.builder()
                .id(1L)
                .phone("01012345678")
                .created(now)
                .modified(now)
                .isRemoved(false)
                .build();

        // When
        var entity = bannedPhone.toEntity();

        // Then
        assertThat(entity)
                .satisfies(e -> {
                    assertThat(e.getId()).isEqualTo(bannedPhone.getId());
                    assertThat(e.getPhone()).isEqualTo(bannedPhone.getPhone());
                });
    }
}