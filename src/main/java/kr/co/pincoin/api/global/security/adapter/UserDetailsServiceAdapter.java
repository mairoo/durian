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
@Transactional(readOnly = true) // 장점 1. 조회 전용 트랜잭션 적용
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceAdapter implements UserDetailsService {
  // 단방향 mapper 변환기: 인터페이스 불일치 해결 (상대가 필요로 하는 걸 내가 가진 걸로 채워줌)
  // 도메인 계층 UserRepository.findByEmail -> 어댑터 -> 스프링 시큐리티 UserDetailsService.loadUserByUsername

  // 어댑터 패턴 도입의 이유
  //
  // 🤔 문제: 두 인터페이스가 호환되지 않음
  // - 메서드 이름이 다름 (loadUserByUsername vs findByEmail)
  // - 반환 타입이 다름 (UserDetails vs Optional<User>)

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> {
                  // 장점 2. 도메인 예외를 스프링 시큐리티 예외로 변환
                  log.error("{} email not found", email);
                  return new UsernameNotFoundException(ErrorCode.INVALID_CREDENTIALS.getMessage());
                });
    return new UserDetailsAdapter(user);
  }
}
