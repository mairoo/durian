package kr.co.pincoin.api.global.security.adapter;

import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceAdapter implements UserDetailsService {
    // 단방향 mapper 변환기: 인터페이스 불일치 해결 (상대가 필요로 하는 걸 내가 가진 걸로 채워줌)
    // 도메인 계층 UserRepository.findByEmail -> 어댑터 -> 스프링 시큐리티 UserDetailsService.loadUserByUsername

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("{} email not found", email);
                    return new UsernameNotFoundException(ErrorCode.INVALID_CREDENTIALS.getMessage());
                });
        return new UserDetailsAdapter(user);
    }
}
