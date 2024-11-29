package kr.co.pincoin.api.domain.auth.model.phone.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum PhoneVerifiedStatus {
  UNVERIFIED(0, "휴대폰미인증"),
  VERIFIED(1, "휴대폰인증완료"),
  REVOKED(2, "휴대폰인증취소");

  private final int code;
  private final String description;

  PhoneVerifiedStatus(int code, String description) {
    this.code = code;
    this.description = description;
  }

  public static PhoneVerifiedStatus findByCode(int code) {
    return Arrays.stream(PhoneVerifiedStatus.values())
        .filter(status -> status.getCode() == code)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("Invalid PhoneVerifiedStatus code: " + code));
  }

  public static PhoneVerifiedStatus findByDescription(String description) {
    return Arrays.stream(PhoneVerifiedStatus.values())
        .filter(status -> status.getDescription().equals(description))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Invalid PhoneVerifiedStatus description: " + description));
  }
}
