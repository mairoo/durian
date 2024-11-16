package kr.co.pincoin.api.infra.auth.mapper.user;

import kr.co.pincoin.api.domain.auth.model.user.LoginLog;
import kr.co.pincoin.api.infra.auth.entity.user.LoginLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoginLogMapper {
    private final UserMapper userMapper;

    public LoginLog toModel(LoginLogEntity entity) {
        if (entity == null) {
            return null;
        }

        return LoginLog.from(entity);
    }

    public LoginLogEntity toEntity(LoginLog model) {
        if (model == null) {
            return null;
        }

        return LoginLogEntity.builder()
                .id(model.getId())
                .ipAddress(model.getIpAddress())
                .user(userMapper.toEntity(model.getUser()))
                .build();
    }

    public List<LoginLog> toModelList(List<LoginLogEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<LoginLogEntity> toEntityList(List<LoginLog> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}