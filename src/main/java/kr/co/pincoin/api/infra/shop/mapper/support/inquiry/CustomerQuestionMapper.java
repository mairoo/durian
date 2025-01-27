package kr.co.pincoin.api.infra.shop.mapper.support.inquiry;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.support.inquiry.CustomerQuestion;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import kr.co.pincoin.api.infra.shop.entity.support.inquiry.CustomerQuestionEntity;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerQuestionMapper {
  private final UserMapper userMapper;
  private final OrderMapper orderMapper;

  public CustomerQuestion toModel(CustomerQuestionEntity entity) {
    if (entity == null) {
      return null;
    }

    return CustomerQuestion.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .description(entity.getDescription())
        .keywords(entity.getKeywords())
        .content(entity.getContent())
        .category(entity.getCategory())
        .owner(userMapper.toModel(entity.getOwner()))
        .order(orderMapper.toModel(entity.getOrder()))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .isRemoved(entity.isRemoved())
        .build();
  }

  public List<CustomerQuestion> toModelList(List<CustomerQuestionEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public CustomerQuestionEntity toEntity(CustomerQuestion model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<CustomerQuestionEntity> toEntityList(List<CustomerQuestion> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
