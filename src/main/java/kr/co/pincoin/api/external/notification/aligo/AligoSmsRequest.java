package kr.co.pincoin.api.external.notification.aligo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JSON 역직렬화를 위한 기본 생성자
@AllArgsConstructor(access = AccessLevel.PRIVATE) // @Builder 사용 private 생성자
@Builder
public class AligoSmsRequest {
  @JsonProperty("phone")
  private String phone;

  @JsonProperty("message")
  @Size(max = 1000, message = "메시지는 1000자를 초과할 수 없습니다")
  private String message;
}
