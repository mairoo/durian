package kr.co.pincoin.api.infra.shop.mapper.support.review;

import kr.co.pincoin.api.domain.shop.model.support.review.Testimonial;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import kr.co.pincoin.api.infra.shop.entity.support.review.TestimonialEntity;
import kr.co.pincoin.api.infra.shop.mapper.store.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TestimonialMapper {
    private final UserMapper userMapper;
    private final StoreMapper storeMapper;

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
                .store(storeMapper.toModel(entity.getStore()))
                .created(entity.getCreated())
                .modified(entity.getModified())
                .isRemoved(entity.getIsRemoved())
                .build();
    }

    public TestimonialEntity toEntity(Testimonial model) {
        if (model == null) {
            return null;
        }

        return model.toEntity();
    }

    public List<Testimonial> toModelList(List<TestimonialEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<TestimonialEntity> toEntityList(List<Testimonial> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}