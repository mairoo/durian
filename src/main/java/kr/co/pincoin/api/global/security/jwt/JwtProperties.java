package kr.co.pincoin.api.global.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,

        int accessTokenExpiresIn,

        int refreshTokenExpiresIn,

        String cookieName,

        String cookieDomain,

        String oauth2RedirectUrl
) {
}
