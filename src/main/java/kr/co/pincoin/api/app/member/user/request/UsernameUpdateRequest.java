package kr.co.pincoin.api.app.member.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JSON 역직렬화를 위한 기본 생성자
public class UsernameUpdateRequest {
    @NotBlank(message = "아이디는 필수 입력값입니다")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다")
    @Pattern(
            regexp = "^[a-z0-9][a-z0-9-_]{3,19}$",
            message = "아이디는 영문 소문자, 숫자로 시작하고 영문 소문자, 숫자, 하이픈, 언더스코어만 사용하여 4~20자리여야 합니다"
    )
    @JsonProperty("username")
    private String username;
}
