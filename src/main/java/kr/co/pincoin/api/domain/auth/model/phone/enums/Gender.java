package kr.co.pincoin.api.domain.auth.model.phone.enums;

import lombok.Getter;

@Getter
public enum Gender {
  FEMALE(0, "여자"),
  MALE(1, "남자");

  private final int code;
  private final String description;

  Gender(int code, String description) {
    this.code = code;
    this.description = description;
  }
}
