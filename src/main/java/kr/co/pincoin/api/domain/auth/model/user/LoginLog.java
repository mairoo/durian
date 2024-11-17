package kr.co.pincoin.api.domain.auth.model.user;

import kr.co.pincoin.api.infra.auth.entity.user.LoginLogEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LoginLog {
    private final Long id;

    private final String ipAddress;

    private final User user;

    private final LocalDateTime created;

    private final LocalDateTime modified;

    @Builder
    private LoginLog(Long id,
                     String ipAddress,
                     User user,
                     LocalDateTime created,
                     LocalDateTime modified) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.user = user;
        this.created = created != null
                ? created
                : LocalDateTime.now();
        this.modified = modified != null
                ? modified
                : LocalDateTime.now();
    }

    public static LoginLog of(String ipAddress, User user) {
        return LoginLog.builder()
                .ipAddress(ipAddress)
                .user(user)
                .build();
    }

    public LoginLogEntity toEntity() {
        return LoginLogEntity.builder()
                .id(this.getId())
                .ipAddress(this.getIpAddress())
                .user(this.getUser().toEntity())
                .build();
    }

    public boolean isRecentLogin() {
        return this.created.isAfter(LocalDateTime.now().minusHours(24));
    }

    public boolean isSameUser(User otherUser) {
        return this.user.getId().equals(otherUser.getId());
    }

    public boolean isFromSameIP(String otherIpAddress) {
        return this.ipAddress.equals(otherIpAddress);
    }
}