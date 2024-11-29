package kr.co.pincoin.api.domain.auth.model.phone;

import java.time.LocalDateTime;
import kr.co.pincoin.api.infra.auth.entity.phone.BannedPhoneEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BannedPhone {
  private final Long id;

  private final String phone;

  private final LocalDateTime created;

  private final LocalDateTime modified;

  private boolean isRemoved;

  @Builder
  private BannedPhone(
      Long id, String phone, LocalDateTime created, LocalDateTime modified, boolean isRemoved) {
    this.id = id;
    this.phone = phone;
    this.created = created;
    this.modified = modified;
    this.isRemoved = isRemoved;
  }

  public static BannedPhone of(String phone) {
    return BannedPhone.builder().phone(phone).build();
  }

  public BannedPhoneEntity toEntity() {
    return BannedPhoneEntity.builder().id(this.getId()).phone(this.getPhone()).build();
  }

  public void softDelete() {
    this.isRemoved = true;
  }

  public void restore() {
    this.isRemoved = false;
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
