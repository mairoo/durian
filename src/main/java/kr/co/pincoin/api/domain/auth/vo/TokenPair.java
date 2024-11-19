package kr.co.pincoin.api.domain.auth.vo;

import kr.co.pincoin.api.app.auth.response.AccessTokenResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenPair {
    private AccessTokenResponse accessToken; // 클라이언트에 전달될 JSON 응답

    private String refreshToken; // 쿠키에 설정될 토큰 (nullable)
}