package kr.co.pincoin.api.infra.auth.mapper.user;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.auth.model.user.LoginLog;
import kr.co.pincoin.api.infra.auth.entity.user.LoginLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginLogMapper {
  private final UserMapper userMapper;

  public LoginLog toModel(LoginLogEntity entity) {
    if (entity == null) {
      return null;
    }

    return LoginLog.builder()
        .id(entity.getId())
        .ipAddress(entity.getIpAddress())
        .user(userMapper.toModel(entity.getUser()))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .build();
  }

  public LoginLogEntity toEntity(LoginLog model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<LoginLog> toModelList(List<LoginLogEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public List<LoginLogEntity> toEntityList(List<LoginLog> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
