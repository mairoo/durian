package kr.co.pincoin.api.infra.auth.mapper.user;


import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public User toModel(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.from(entity);
    }

    public UserEntity toEntity(User model) {
        if (model == null) {
            return null;
        }

        return UserEntity.builder()
                .id(model.getId())
                .password(model.getPassword())
                .lastLogin(model.getLastLogin())
                .isSuperuser(model.isSuperuser())
                .username(model.getUsername())
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .email(model.getEmail())
                .isStaff(model.isStaff())
                .isActive(model.isActive())
                .dateJoined(model.getDateJoined())
                .build();
    }

    // 컬렉션 지원
    public List<User> toModelList(List<UserEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<UserEntity> toEntityList(List<User> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
