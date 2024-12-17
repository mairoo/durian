package kr.co.pincoin.api.global.security.authorization;

import kr.co.pincoin.api.domain.auth.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurityRule {

    public boolean isSuperUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication.getPrincipal() instanceof User) {
            return ((User) authentication.getPrincipal()).isSuperuser();
        }

        return false;
    }
}