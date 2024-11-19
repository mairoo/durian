package kr.co.pincoin.api.domain.auth.vo;

import java.time.LocalDateTime;
import java.util.Objects;

public record AccessToken(
        String token,
        LocalDateTime expiresAt
) {
    // 유효성 검증 compact 생성자
    public AccessToken {
        Objects.requireNonNull(token, "Token must not be null");
        Objects.requireNonNull(expiresAt, "Expiry date must not be null");
    }

    // 도메인 로직
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}