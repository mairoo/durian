package kr.co.pincoin.api.app.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.pincoin.api.domain.auth.vo.AccessToken;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessTokenResponse {
    @JsonProperty("accessToken")
    private final String accessToken;

    private AccessTokenResponse(AccessToken accessToken) {
        this.accessToken = accessToken.token();
    }

    public static AccessTokenResponse from(AccessToken accessToken) {
        return new AccessTokenResponse(accessToken);
    }
}
