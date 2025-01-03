package kr.co.pincoin.api.infra.shop.mapper.support.review;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.support.review.Testimonial;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import kr.co.pincoin.api.infra.shop.entity.support.review.TestimonialEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestimonialMapper {
  private final UserMapper userMapper;

  public Testimonial toModel(TestimonialEntity entity) {
    if (entity == null) {
      return null;
    }

    return Testimonial.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .description(entity.getDescription())
        .keywords(entity.getKeywords())
        .content(entity.getContent())
        .owner(userMapper.toModel(entity.getOwner()))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .isRemoved(entity.isRemoved())
        .build();
  }

  public List<Testimonial> toModelList(List<TestimonialEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public TestimonialEntity toEntity(Testimonial model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<TestimonialEntity> toEntityList(List<Testimonial> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
