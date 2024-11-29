package kr.co.pincoin.api.domain.shop.model.support.message;

import java.time.LocalDateTime;
import kr.co.pincoin.api.infra.shop.entity.support.message.SmsEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Sms {
  private final Long id;
  private final String phoneFrom;
  private final String phoneTo;
  private final String content;
  private final LocalDateTime created;
  private final LocalDateTime modified;
  private Boolean success;

  @Builder
  private Sms(
      Long id,
      String phoneFrom,
      String phoneTo,
      String content,
      Boolean success,
      LocalDateTime created,
      LocalDateTime modified) {
    this.id = id;
    this.phoneFrom = phoneFrom;
    this.phoneTo = phoneTo;
    this.content = content;
    this.success = success;
    this.created = created;
    this.modified = modified;

    validateSms();
  }

  public SmsEntity toEntity() {
    return SmsEntity.builder()
        .id(this.getId())
        .phoneFrom(this.getPhoneFrom())
        .phoneTo(this.getPhoneTo())
        .content(this.getContent())
        .success(this.getSuccess())
        .build();
  }

  public static Sms of(String phoneFrom, String phoneTo, String content) {
    return Sms.builder().phoneFrom(phoneFrom).phoneTo(phoneTo).content(content).build();
  }

  public void markAsSuccess() {
    this.success = true;
  }

  public void markAsFailed() {
    this.success = false;
  }

  public boolean isDelivered() {
    return this.success;
  }

  public boolean isPending() {
    return !this.success;
  }

  public boolean isRecent() {
    return this.created.isAfter(LocalDateTime.now().minusHours(24));
  }

  public int getContentLength() {
    return this.content != null ? this.content.length() : 0;
  }

  private void validateSms() {
    validatePhoneNumber(this.phoneFrom, "Sender");
    validatePhoneNumber(this.phoneTo, "Recipient");
    validateContent(this.content);
  }

  private void validatePhoneNumber(String phoneNumber, String type) {
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
      throw new IllegalArgumentException(type + " phone number cannot be empty");
    }

    // 전화번호 형식 검증 (숫자와 하이픈만 허용)
    if (!phoneNumber.matches("^[0-9-]+$")) {
      throw new IllegalArgumentException(
          type + " phone number must contain only digits and hyphens");
    }

    // 하이픈을 제거한 숫자만으로 길이 검증
    String digitsOnly = phoneNumber.replaceAll("-", "");
    if (digitsOnly.length() < 10 || digitsOnly.length() > 11) {
      throw new IllegalArgumentException(type + " phone number must be 10-11 digits long");
    }
  }

  private void validateContent(String content) {
    if (content == null || content.trim().isEmpty()) {
      throw new IllegalArgumentException("SMS content cannot be empty");
    }

    if (content.length() > 2000) {
      throw new IllegalArgumentException("SMS content cannot exceed 2000 characters");
    }
  }

  public enum SmsType {
    VERIFICATION(1),
    NOTIFICATION(2),
    MARKETING(3),
    ALERT(4);

    private final Integer value;

    SmsType(Integer value) {
      this.value = value;
    }

    public static SmsType fromValue(Integer value) {
      for (SmsType type : SmsType.values()) {
        if (type.value.equals(value)) {
          return type;
        }
      }
      throw new IllegalArgumentException("Invalid SMS type value: " + value);
    }

    public Integer getValue() {
      return value;
    }
  }
}
