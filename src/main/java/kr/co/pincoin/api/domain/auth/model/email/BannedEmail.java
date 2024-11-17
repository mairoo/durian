package kr.co.pincoin.api.domain.auth.model.email;

import kr.co.pincoin.api.infra.auth.entity.email.BannedEmailEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BannedEmail {
    private final Long id;

    private final String email;

    private final LocalDateTime created;

    private final LocalDateTime modified;

    private boolean isRemoved;

    @Builder
    private BannedEmail(Long id,
                        String email,
                        LocalDateTime created,
                        LocalDateTime modified,
                        boolean isRemoved) {
        this.id = id;
        this.email = email;
        this.created = created;
        this.modified = modified;
        this.isRemoved = isRemoved;
    }

    public static BannedEmail of(String email) {
        return BannedEmail.builder()
                .email(email)
                .build();
    }

    public BannedEmailEntity toEntity() {
        return BannedEmailEntity.builder()
                .id(this.getId())
                .email(this.getEmail())
                .build();
    }

    public void remove() {
        this.isRemoved = true;
    }

    public void restore() {
        this.isRemoved = false;
    }

    public boolean isActive() {
        return !this.isRemoved;
    }

    public boolean matches(String email) {
        if (email == null || isRemoved) {
            return false;
        }

        // 정확한 이메일 매칭
        if (this.email.equalsIgnoreCase(email)) {
            return true;
        }

        // 와일드카드 도메인 매칭 (예: *@domain.com)
        if (this.email.startsWith("*@")) {
            String domain = this.email.substring(2);
            return email.toLowerCase().endsWith("@" + domain.toLowerCase());
        }

        return false;
    }
}