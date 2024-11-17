package kr.co.pincoin.api.infra.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    // CustomUserDetailsService
    // - 스프링 시큐리티의 기본 구현체와 구분됨
    // - 우리 도메인에 맞게 커스터마이징했다는 의도 표현
    // - 스프링 생태계에서 자주 사용되는 관행
    //
    // UserDetailsServiceImpl
    // - 구현 세부사항만 강조 표현
    // - 해당 클래스의 실제 역할이나 의도 표현 못함
    // - 일반적으로 좋지 않은 네이밍 관행

    @Override
    public UserDetails loadUserByUsername(String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException(username));
//
//        return new UserSecurityAdapter(user);
        return null;
    }
}
