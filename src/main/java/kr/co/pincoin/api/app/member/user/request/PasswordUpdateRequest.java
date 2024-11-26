package kr.co.pincoin.api.app.member.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JSON 역직렬화를 위한 기본 생성자
@AllArgsConstructor(access = AccessLevel.PRIVATE)   // @Builder 사용 private 생성자
@Builder
public class PasswordUpdateRequest {
    @NotBlank(message = "현재 비밀번호는 필수 입력값입니다")
    @JsonProperty("currentPassword")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수 입력값입니다")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "새 비밀번호는 8~30자리이면서 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    @JsonProperty("newPassword")
    private String newPassword;
}
