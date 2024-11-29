package kr.co.pincoin.api.global.security.adapter;

import java.util.Collection;
import java.util.List;
import kr.co.pincoin.api.domain.auth.model.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record UserDetailsAdapter(User user) implements UserDetails {
  // 단방향 mapper 변환기: 인터페이스 불일치 해결 (상대가 필요로 하는 걸 내가 가진 걸로 채워줌)
  // 도메인 계층 User -> 어댑터 -> 스프링 시큐리티 UserDetails
  // @Component 필요 없음 : UserDetailsServiceAdapter에서 new 키워드 인스턴스 생성

  // 어댑터 패턴 도입의 이유
  //
  // 나쁜 예:
  // public class User implements UserDetails { // 🚫 도메인이 외부 프레임워크에 오염됨
  // 좋은 예:
  // public class User { // ✅ 순수한 도메인 모델 유지
  // public class UserDetailsAdapter implements UserDetails { // ✅ 외부 의존성을 어댑터가 처리

  // 주의사항
  // - 어댑터에 비즈니스 로직을 넣지 않는다. 단순 변환만 담당

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
