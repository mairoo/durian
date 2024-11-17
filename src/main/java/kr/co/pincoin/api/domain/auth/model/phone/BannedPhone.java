package kr.co.pincoin.api.domain.auth.model.phone;

import kr.co.pincoin.api.infra.auth.entity.phone.BannedPhoneEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BannedPhone {
    private final Long id;

    private String phone;

    private final LocalDateTime created;

    private final LocalDateTime modified;

    private boolean isRemoved;

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "instanceBuilder")
    private BannedPhone(String phone) {
        this.id = null;
        this.phone = phone;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
        this.isRemoved = false;
    }

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "jpaBuilder")
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

    public static BannedPhone of(String phone) {
        return BannedPhone.instanceBuilder()
                .phone(phone)
                .build();
    }

    public static BannedPhone from(BannedPhoneEntity entity) {
        return BannedPhone.jpaBuilder()
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

    private String normalizePhoneNumber(String phone) {
        return phone.replaceAll("[^0-9]", "");
    }
}