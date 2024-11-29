package kr.co.pincoin.api.external.notification.mailgun;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JSON 역직렬화를 위한 기본 생성자
public class MailgunEmailRequest {
  @NotBlank(message = "이메일은 필수 입력값입니다")
  @Email(message = "올바른 이메일 형식이 아닙니다")
  @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
  @JsonProperty("email")
  private String email;
}
