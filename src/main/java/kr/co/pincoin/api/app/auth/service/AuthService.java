package kr.co.pincoin.api.app.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.pincoin.api.app.auth.request.SignInRequest;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.domain.auth.vo.TokenPair;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Optional<TokenPair>
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

        // 리프레시 토큰 발급

        // 토큰 쌍 반환

        return Optional.of(new TokenPair("access token", "refresh token", LocalDateTime.now()));
    }

    public void logout() {

    }

    public void refresh() {

    }
}
