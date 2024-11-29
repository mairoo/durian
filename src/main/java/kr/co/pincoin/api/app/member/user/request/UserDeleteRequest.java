package kr.co.pincoin.api.app.member.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JSON 역직렬화를 위한 기본 생성자
public class UserDeleteRequest {
  @NotBlank(message = "비밀번호는 필수 입력값입니다")
  @JsonProperty("password")
  private String password;
}
