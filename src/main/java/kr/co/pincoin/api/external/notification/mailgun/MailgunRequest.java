package kr.co.pincoin.api.external.notification.mailgun;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MailgunRequest {

  @NotBlank(message = "수신자 이메일은 필수입니다")
  @Email(message = "올바른 이메일 형식이 아닙니다")
  private String to;

  @NotBlank(message = "제목은 필수입니다")
  @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
  private String subject;

  @NotBlank(message = "본문은 필수입니다")
  @Size(max = 10000, message = "본문은 10000자를 초과할 수 없습니다")
  private String text;

  @Size(max = 50000, message = "HTML 본문은 50000자를 초과할 수 없습니다")
  private String html;
}
