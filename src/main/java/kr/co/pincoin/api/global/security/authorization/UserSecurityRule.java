package kr.co.pincoin.api.global.security.authorization;

import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.global.security.adapter.UserDetailsAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSecurityRule {

  public boolean isSuperUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    if (authentication.getPrincipal() instanceof UserDetailsAdapter(User user)) {
      return user.isSuperuser();
    }

    return false;
  }
}