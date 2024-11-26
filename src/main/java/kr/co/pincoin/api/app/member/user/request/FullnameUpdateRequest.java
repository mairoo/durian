package kr.co.pincoin.api.app.member.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JSON 역직렬화를 위한 기본 생성자
public class FullnameUpdateRequest {
    @NotBlank(message = "이름을 입력해주세요")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요")
    private String firstName;

    @NotBlank(message = "성을 입력해주세요")
    @Size(min = 2, max = 50, message = "성은 2자 이상 50자 이하로 입력해주세요")
    private String lastName;
}
