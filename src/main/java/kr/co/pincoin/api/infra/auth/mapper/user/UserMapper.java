package kr.co.pincoin.api.infra.auth.mapper.user;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public User toModel(UserEntity entity) {
    if (entity == null) {
      return null;
    }

    return User.builder()
        .id(entity.getId())
        .password(entity.getPassword())
        .lastLogin(entity.getLastLogin())
        .isSuperuser(entity.getIsSuperuser())
        .username(entity.getUsername())
        .firstName(entity.getFirstName())
        .lastName(entity.getLastName())
        .email(entity.getEmail())
        .isStaff(entity.getIsStaff())
        .isActive(entity.getIsActive())
        .dateJoined(entity.getDateJoined())
        .build();
  }

  public UserEntity toEntity(User model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  // 컬렉션 지원
  public List<User> toModelList(List<UserEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public List<UserEntity> toEntityList(List<User> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
