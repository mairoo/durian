package kr.co.pincoin.api.infra.shop.mapper.product;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.infra.shop.entity.product.CategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {
  public Category toModel(CategoryEntity entity) {
    if (entity == null) {
      return null;
    }

    return Category.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .slug(entity.getSlug())
        .thumbnail(entity.getThumbnail())
        .description(entity.getDescription())
        .description1(entity.getDescription1())
        .discountRate(entity.getDiscountRate())
        .pg(entity.getPg())
        .pgDiscountRate(entity.getPgDiscountRate())
        .naverSearchTag(entity.getNaverSearchTag())
        .naverBrandName(entity.getNaverBrandName())
        .naverMakerName(entity.getNaverMakerName())
        .lft(entity.getLft())
        .rght(entity.getRght())
        .treeId(entity.getTreeId())
        .level(entity.getLevel())
        .created(entity.getCreated())
        .modified(entity.getModified())
        .parent(Optional.ofNullable(entity.getParent()).map(this::toModel).orElse(null))
        .build();
  }

  public CategoryEntity toEntity(Category model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<Category> toModelList(List<CategoryEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public List<CategoryEntity> toEntityList(List<Category> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
