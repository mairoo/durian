package kr.co.pincoin.api.domain.auth.vo;

import java.time.LocalDateTime;
import java.util.Objects;

public record TokenPair(
        String accessToken,
        String refreshToken,
        LocalDateTime expiresAt
) {
    // 유효성 검증 compact 생성자
    public TokenPair {
        Objects.requireNonNull(accessToken, "Token must not be null");
        Objects.requireNonNull(expiresAt, "Expiry date must not be null");
    }

    // 도메인 로직
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}