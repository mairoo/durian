package kr.co.pincoin.api.app.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.pincoin.api.app.auth.request.SignInRequest;
import kr.co.pincoin.api.app.auth.response.AccessTokenResponse;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.domain.auth.vo.TokenPair;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.global.exception.JwtAuthenticationException;
import kr.co.pincoin.api.global.security.jwt.JwtProperties;
import kr.co.pincoin.api.global.security.jwt.JwtTokenProvider;
import kr.co.pincoin.api.global.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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

    /**
     * 사용자 로그인 처리 및 토큰 발급
     *
     * @param request        로그인 요청 정보 (이메일, 비밀번호, 자동 로그인 여부)
     * @param servletRequest IP 주소 확인용 HTTP 요청 객체
     * @return TokenPair (액세스 토큰, 리프레시 토큰)
     * @throws BusinessException 이메일이나 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    public TokenPair login(SignInRequest request, HttpServletRequest servletRequest) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = null;

        // 자동 로그인 요청시 리프레시 토큰 발급
        if (request.isRememberMe()) {
            // 기존 리프레시 토큰 삭제
            String oldRefreshToken = redisTemplate.opsForValue().get(user.getEmail());
            if (oldRefreshToken != null) {
                redisTemplate.delete(oldRefreshToken);
            }

            refreshToken = jwtTokenProvider.createRefreshToken();
            saveRefreshTokenInfo(refreshToken, user.getEmail(), servletRequest);
        }

        return new TokenPair(new AccessTokenResponse(accessToken, jwtProperties.accessTokenExpiresIn()), refreshToken);
    }

    /**
     * 로그아웃 처리
     * Redis에서 리프레시 토큰과 관련 정보 삭제
     */
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            String email = (String) redisTemplate.opsForHash().get(refreshToken, "email");
            if (email != null) {
                redisTemplate.delete(refreshToken);
                redisTemplate.delete(email);
            }
        }
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 토큰 쌍 발급
     *
     * @param refreshToken   기존 리프레시 토큰
     * @param servletRequest IP 주소 확인용 HTTP 요청 객체
     * @return TokenPair (새로운 액세스 토큰, 새로운 리프레시 토큰)
     * @throws JwtAuthenticationException 토큰이 유효하지 않거나 IP가 불일치하는 경우
     */
    @Transactional
    public TokenPair refresh(String refreshToken, HttpServletRequest servletRequest) {
        validateRefreshToken(refreshToken, servletRequest);

        String email = (String) redisTemplate.opsForHash().get(refreshToken, "email");
        String newAccessToken = jwtTokenProvider.createAccessToken(email);
        String newRefreshToken = jwtTokenProvider.createRefreshToken();

        redisTemplate.delete(refreshToken);
        saveRefreshTokenInfo(newRefreshToken, email, servletRequest);

        return new TokenPair(new AccessTokenResponse(newAccessToken, jwtProperties.accessTokenExpiresIn()),
                             newRefreshToken);
    }

    /**
     * Redis에 리프레시 토큰 관련 정보 저장
     * - 리프레시 토큰을 키로 하는 해시: 이메일, IP 주소 저장
     * - 이메일을 키로 하는 문자열: 리프레시 토큰 저장
     */
    private void saveRefreshTokenInfo(String refreshToken, String email, HttpServletRequest request) {
        String clientIp = IpUtils.getClientIp(request);
        redisTemplate.opsForHash().put(refreshToken, "email", email);
        redisTemplate.opsForHash().put(refreshToken, "ipAddress", clientIp);
        redisTemplate.expire(refreshToken, jwtProperties.refreshTokenExpiresIn(), TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(email, refreshToken, jwtProperties.refreshTokenExpiresIn(), TimeUnit.SECONDS);
    }

    /**
     * 리프레시 토큰 유효성 검증
     * - UUID 형식 검증
     * - Redis에 저장된 토큰인지 확인
     * - 요청 IP와 저장된 IP 일치 여부 확인
     */
    private void validateRefreshToken(String refreshToken, HttpServletRequest request) {
        if (refreshToken == null || !refreshToken.matches(
                "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
            throw new JwtAuthenticationException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = (String) redisTemplate.opsForHash().get(refreshToken, "email");
        if (email == null) {
            throw new JwtAuthenticationException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String storedIp = (String) redisTemplate.opsForHash().get(refreshToken, "ipAddress");
        String currentIp = IpUtils.getClientIp(request);
        if (storedIp == null || !storedIp.equals(currentIp)) {
            throw new JwtAuthenticationException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
