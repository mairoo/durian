package kr.co.pincoin.api.infra.shop.mapper.support.message;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.support.message.NoticeMessage;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import kr.co.pincoin.api.infra.shop.entity.support.message.NoticeMessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeMessageMapper {
  private final UserMapper userMapper;

  public NoticeMessage toModel(NoticeMessageEntity entity) {
    if (entity == null) {
      return null;
    }

    return NoticeMessage.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .description(entity.getDescription())
        .keywords(entity.getKeywords())
        .content(entity.getContent())
        .category(entity.getCategory())
        .owner(userMapper.toModel(entity.getOwner()))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .isRemoved(entity.isRemoved())
        .build();
  }

  public List<NoticeMessage> toModelList(List<NoticeMessageEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public NoticeMessageEntity toEntity(NoticeMessage model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<NoticeMessageEntity> toEntityList(List<NoticeMessage> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
