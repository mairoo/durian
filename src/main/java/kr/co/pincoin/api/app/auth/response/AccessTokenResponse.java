package kr.co.pincoin.api.app.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.pincoin.api.domain.auth.vo.TokenPair;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessTokenResponse {
    @JsonProperty("accessToken")
    private final String accessToken;

    private AccessTokenResponse(TokenPair tokenPair) {
        this.accessToken = tokenPair.accessToken();
    }

    public static AccessTokenResponse from(TokenPair tokenPair) {
        return new AccessTokenResponse(tokenPair);
    }
}
