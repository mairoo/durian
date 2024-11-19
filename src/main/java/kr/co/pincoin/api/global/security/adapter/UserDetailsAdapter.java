package kr.co.pincoin.api.global.security.adapter;

import kr.co.pincoin.api.domain.auth.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class UserDetailsAdapter implements UserDetails {
    // 단방향 mapper 변환기: 인터페이스 불일치 해결 (상대가 필요로 하는 걸 내가 가진 걸로 채워줌)
    // 도메인 계층 User -> 어댑터 -> 스프링 시큐리티 UserDetails
    // @Component 필요 없음 : UserDetailsServiceAdapter에서 new 키워드 인스턴스 생성
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = user.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER";

        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
