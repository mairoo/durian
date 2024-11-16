package kr.co.pincoin.api.domain.auth.model.phone;

import kr.co.pincoin.api.infra.auth.entity.phone.BannedPhoneEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BannedPhone {
    private final Long id;

    private final LocalDateTime created;

    private final LocalDateTime modified;

    private String phone;

    private boolean isRemoved;

    @Builder(access = AccessLevel.PRIVATE)
    private BannedPhone(Long id,
                        String phone,
                        LocalDateTime created,
                        LocalDateTime modified,
                        boolean isRemoved) {
        this.id = id;
        this.phone = phone;
        this.created = created;
        this.modified = modified;
        this.isRemoved = isRemoved;
    }

    // 새로운 차단 전화번호 생성
    public static BannedPhone of(String phone) {
        return BannedPhone.builder()
                .phone(phone)
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .isRemoved(false)
                .build();
    }

    // 엔티티로부터 도메인 모델 생성
    public static BannedPhone from(BannedPhoneEntity entity) {
        return BannedPhone.builder()
                .id(entity.getId())
                .phone(entity.getPhone())
                .created(entity.getCreated())
                .modified(entity.getModified())
                .isRemoved(entity.getIsRemoved())
                .build();
    }

    public void remove() {
        this.isRemoved = true;
    }

    public void restore() {
        this.isRemoved = false;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return !this.isRemoved;
    }

    public boolean matches(String phone) {
        if (phone == null || isRemoved) {
            return false;
        }

        // 전화번호 정규화 (특수문자 제거)
        String normalizedInput = normalizePhoneNumber(phone);
        String normalizedBanned = normalizePhoneNumber(this.phone);

        return normalizedInput.equals(normalizedBanned);
    }

    // 전화번호 정규화 (특수문자 제거)
    private String normalizePhoneNumber(String phone) {
        return phone.replaceAll("[^0-9]", "");
    }
}