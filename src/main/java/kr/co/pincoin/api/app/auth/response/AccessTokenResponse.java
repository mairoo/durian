package kr.co.pincoin.api.app.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessTokenResponse {
    // https://www.oauth.com/oauth2-servers/access-tokens/access-token-response/

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("tokenType")
    private String tokenType;

    @JsonProperty("expiresIn")
    private int expiresIn;

    public static AccessTokenResponse of(String accessToken, int expiresIn) {
        return AccessTokenResponse.builder()
                .tokenType("Bearer") // OAuth 2.0에서 가장 일반적인 타입
                .accessToken(accessToken) // 초 단위의 만료 시간
                .expiresIn(expiresIn).build();
    }
}
