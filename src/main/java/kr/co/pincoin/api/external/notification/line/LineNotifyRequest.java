package kr.co.pincoin.api.external.notification.line;

import jakarta.validation.constraints.NotBlank;
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
public class LineNotifyRequest {

  @NotBlank(message = "메시지는 필수입니다")
  @Size(max = 1000, message = "메시지는 1000자를 초과할 수 없습니다")
  private String message;
}
