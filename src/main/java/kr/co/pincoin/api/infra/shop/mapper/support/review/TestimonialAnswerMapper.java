package kr.co.pincoin.api.infra.shop.mapper.support.review;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.support.review.TestimonialAnswer;
import kr.co.pincoin.api.infra.shop.entity.support.review.TestimonialAnswerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestimonialAnswerMapper {
  private final TestimonialMapper testimonialMapper;

  public TestimonialAnswer toModel(TestimonialAnswerEntity entity) {
    if (entity == null) {
      return null;
    }

    return TestimonialAnswer.builder()
        .id(entity.getId())
        .content(entity.getContent())
        .testimonial(testimonialMapper.toModel(entity.getTestimonial()))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .build();
  }

  public TestimonialAnswerEntity toEntity(TestimonialAnswer model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<TestimonialAnswer> toModelList(List<TestimonialAnswerEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public List<TestimonialAnswerEntity> toEntityList(List<TestimonialAnswer> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
