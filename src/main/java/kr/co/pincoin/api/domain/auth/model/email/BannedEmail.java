package kr.co.pincoin.api.domain.auth.model.email;

import kr.co.pincoin.api.infra.auth.entity.email.BannedEmailEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BannedEmail {
    private final Long id;

    private final LocalDateTime created;

    private final LocalDateTime modified;

    private final String email;

    private boolean isRemoved;

    @Builder(access = AccessLevel.PRIVATE)
    private BannedEmail(Long id, String email, LocalDateTime created, LocalDateTime modified, boolean isRemoved) {
        this.id = id;
        this.email = email;
        this.created = created;
        this.modified = modified;
        this.isRemoved = isRemoved;
    }

    // 새로운 차단 이메일 생성
    public static BannedEmail of(String email) {
        return BannedEmail.builder().email(email).created(LocalDateTime.now()).modified(LocalDateTime.now())
                .isRemoved(false).build();
    }

    // 엔티티로부터 도메인 모델 생성
    public static BannedEmail from(BannedEmailEntity entity) {
        return BannedEmail.builder().id(entity.getId()).email(entity.getEmail()).created(entity.getCreated())
                .modified(entity.getModified()).isRemoved(entity.getIsRemoved()).build();
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