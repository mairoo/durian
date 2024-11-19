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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final JwtProperties jwtProperties;

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
            refreshToken = jwtTokenProvider.createRefreshToken();

            // 리프레시 토큰 저장
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

    public void refresh() {
    }
}
