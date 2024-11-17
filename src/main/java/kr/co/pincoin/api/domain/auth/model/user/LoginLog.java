package kr.co.pincoin.api.domain.auth.model.user;

import kr.co.pincoin.api.infra.auth.entity.user.LoginLogEntity;
import lombok.AccessLevel;
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

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "instanceBuilder")
    private LoginLog(String ipAddress,
                     User user) {
        this.id = null;
        this.ipAddress = ipAddress;
        this.user = user;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "jpaBuilder")
    private LoginLog(Long id,
                     String ipAddress,
                     User user,
                     LocalDateTime created,
                     LocalDateTime modified) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.user = user;
        this.created = created;
        this.modified = modified;
    }

    public static LoginLog of(String ipAddress, User user) {
        return LoginLog.instanceBuilder()
                .ipAddress(ipAddress)
                .user(user)
                .build();
    }

    public static LoginLog from(LoginLogEntity entity) {
        return LoginLog.jpaBuilder()
                .id(entity.getId())
                .ipAddress(entity.getIpAddress())
                .user(User.from(entity.getUser()))
                .created(entity.getCreated())
                .modified(entity.getModified())
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