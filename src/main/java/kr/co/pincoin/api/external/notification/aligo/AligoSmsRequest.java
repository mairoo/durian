package kr.co.pincoin.api.external.notification.aligo;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JSON 역직렬화를 위한 기본 생성자
@AllArgsConstructor(access = AccessLevel.PRIVATE) // @Builder 사용 private 생성자
@Builder
public class AligoSmsRequest {

  @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "올바른 휴대전화 번호 형식이 아닙니다 (하이픈 제외 10-11자리)")
  private String receiver;

  @Size(max = 1000, message = "메시지는 1000자를 초과할 수 없습니다")
  private String message;
}
