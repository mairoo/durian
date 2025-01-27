package kr.co.pincoin.api.infra.shop.mapper.support.message;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.support.message.FaqMessage;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import kr.co.pincoin.api.infra.shop.entity.support.message.FaqMessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FaqMessageMapper {
  private final UserMapper userMapper;

  public FaqMessage toModel(FaqMessageEntity entity) {
    if (entity == null) {
      return null;
    }

    return FaqMessage.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .description(entity.getDescription())
        .keywords(entity.getKeywords())
        .content(entity.getContent())
        .category(entity.getCategory())
        .position(entity.getPosition())
        .owner(userMapper.toModel(entity.getOwner()))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .isRemoved(entity.isRemoved())
        .build();
  }

  public List<FaqMessage> toModelList(List<FaqMessageEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public FaqMessageEntity toEntity(FaqMessage model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<FaqMessageEntity> toEntityList(List<FaqMessage> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
