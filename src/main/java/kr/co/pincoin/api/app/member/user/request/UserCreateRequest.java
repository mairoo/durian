package kr.co.pincoin.api.app.member.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JSON 역직렬화를 위한 기본 생성자
public class UserCreateRequest {
    @NotBlank(message = "이메일은 필수 입력값입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "비밀번호는 8~30자리이면서 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "아이디는 필수 입력값입니다")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다")
    @Pattern(
            regexp = "^[a-z0-9][a-z0-9-_]{3,19}$",
            message = "아이디는 영문 소문자, 숫자로 시작하고 영문 소문자, 숫자, 하이픈, 언더스코어만 사용하여 4~20자리여야 합니다"
    )
    @JsonProperty("username")
    private String username;

    @NotBlank(message = "이름을 입력해주세요")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요")
    @JsonProperty("firstName")
    private String firstName;

    @NotBlank(message = "성을 입력해주세요")
    @Size(min = 2, max = 50, message = "성은 2자 이상 50자 이하로 입력해주세요")
    @JsonProperty("lastName")
    private String lastName;
}
