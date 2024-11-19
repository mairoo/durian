package kr.co.pincoin.api.app.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.pincoin.api.app.auth.request.SignInRequest;
import kr.co.pincoin.api.app.auth.response.AccessTokenResponse;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.domain.auth.vo.TokenPair;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import kr.co.pincoin.api.global.security.jwt.JwtProperties;
import kr.co.pincoin.api.global.security.jwt.JwtTokenProvider;
import kr.co.pincoin.api.global.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final JwtProperties jwtProperties;

    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public TokenPair
    login(SignInRequest request,
          HttpServletRequest servletRequest) {
        // 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("{} not found", request.getEmail());
                    return new BusinessException(ErrorCode.INVALID_CREDENTIALS);
                });

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("{} password mismatch", request.getEmail());
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 액세스 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());

        // 리프레시 토큰 발급
        String refreshToken = null;

        if (request.isRememberMe()) {
            // 기존 refresh token 있으면 삭제
            String oldRefreshToken = redisTemplate.opsForValue().get(user.getEmail());
            if (oldRefreshToken != null) {
                redisTemplate.delete(oldRefreshToken);
            }

            // 새로운 refresh token 생성 및 저장
            refreshToken = jwtTokenProvider.createRefreshToken();

            // refresh token을 key로 하는 상세 정보 저장
            redisTemplate.opsForHash().put(refreshToken, "email", user.getEmail());
            redisTemplate.opsForHash().put(refreshToken, "ipAddress", IpUtils.getClientIp(servletRequest));
            redisTemplate.expire(refreshToken, jwtProperties.refreshTokenExpiresIn(), TimeUnit.SECONDS);

            // email을 key로 하는 refresh token 저장
            redisTemplate.opsForValue().set(
                    user.getEmail(),
                    refreshToken,
                    jwtProperties.refreshTokenExpiresIn(),
                    TimeUnit.SECONDS);
        }

        // 토큰 쌍 반환
        return new TokenPair(new AccessTokenResponse(accessToken, jwtProperties.accessTokenExpiresIn()),
                             refreshToken);
    }

    public ResponseEntity<ApiResponse<Void>> logout() {
        HttpHeaders headers = new HttpHeaders();

        ResponseCookie refreshTokenCookie = ResponseCookie.from(jwtProperties.cookieName(), "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .domain(jwtProperties.cookieDomain())
                .build();

        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // DB에 저장된 리프레시 토큰은 주기적인 배치 삭제 처리할 것
        return ResponseEntity.ok().headers(headers).body(ApiResponse.of(null));
    }

    public TokenPair refresh() {
        // 리프레시 토큰 조회

        // 기존 리프레시 토큰 즉시 삭제

        // 사용자 조회

        // 액세스 토큰 재발급

        // 리프레시 토큰 재발급 (로테이션 처리)

        // 토큰 쌍 반환

        return null;
    }
}
