package kr.co.pincoin.api.app.member.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordUpdateRequest {
    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "비밀번호는 8~30자리이면서 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    @JsonProperty("password")
    private String password;
}
