package kr.co.pincoin.api.app.member.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.pincoin.api.domain.auth.model.user.User;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    @JsonProperty("id")
    private final Integer id;

    @JsonProperty("username")
    private final String username;

    // 생성자 외부 접근 불허
    private UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

    // 도메인 모델 객체에서 응답 객체 초기화
    public static UserResponse from(User user) {
        return new UserResponse(user);
    }
}