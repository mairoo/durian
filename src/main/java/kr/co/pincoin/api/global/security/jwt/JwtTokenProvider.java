package kr.co.pincoin.api.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.global.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    public static final String JWT_TYPE = "JWT";

    public static final String JWT_ALGORITHM = "HS512";

    private final JwtProperties jwtProperties;

    private SecretKey key;

    private Map<String, Object> headers;

    @PostConstruct
    // 빈 컴포넌트 객체 생성자 후작업으로 멤버 변수 초기화
    public void
    init() {
        // SecretKey key = Jwts.SIG.HS512.key().build();
        // 위와 같이 키를 생성할 경우 서버 재부팅 시 액세스 토큰 검증 문제가 있을 수 있다.
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret()));

        headers = new HashMap<>();
        headers.put("typ", JWT_TYPE);
        headers.put("alg", JWT_ALGORITHM);
    }

    public String
    createAccessToken(String subject) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProperties.accessTokenExpiresIn() * 1000L);

        // 예약 클레임
        // iss, sub, aud, exp, nbf, iat, jti

        // 커스텀 클레임 설정
        // HashMap<String, String> claims = new HashMap<>();
        // claims.put("username", subject);

        return Jwts.builder()
                .header()
                .add(headers)
                .and()
                .subject(subject) // 액세스 토큰에만 sub = username 존재
                // .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    public String
    createRefreshToken() {
        // 리프레시 토큰은 username 같은 개인정보를 담고 있지 않다.
        // 리프레시 토큰은 어차피 서버 DB에 저장하고 검증하므로, JWT의 자체 검증 기능이 불필요하다.
        // 랜덤 문자열은 필요할 때 즉시 폐기하거나 새로 발급할 수 있다.
        // DB에서 관리하므로 만료 시간도 더 유연하게 설정 가능하다.

        return UUID.randomUUID().toString();
    }

    public Optional<String>
    validateToken(String jws) {
        try {
            Jws<Claims> parsed = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jws);

            // 커스텀 클레임 읽을 때 타입 명시
            // String username = parsed.getPayload().get("username", String.class);

            return Optional.ofNullable(parsed.getPayload().getSubject());
        } catch (SignatureException | DecodingException ex) {
            // 잘못된 비밀키
            throw new JwtAuthenticationException(ErrorCode.INVALID_TOKEN);

        } catch (ExpiredJwtException ex) {
            // 만료된 토큰
            throw new JwtAuthenticationException(ErrorCode.EXPIRED_TOKEN);

        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException ex) {
            // 토큰 형식 오류
            throw new JwtAuthenticationException(ErrorCode.INVALID_TOKEN);
        }
    }
}
