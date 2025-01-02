package kr.co.pincoin.api.infra.shop.mapper.product;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.infra.shop.entity.product.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {
  private final CategoryMapper categoryMapper;

  public Product toModel(ProductEntity entity) {
    if (entity == null) {
      return null;
    }

    return Product.builder()
        .id(entity.getId())
        .name(entity.getName())
        .subtitle(entity.getSubtitle())
        .code(entity.getCode())
        .listPrice(entity.getListPrice())
        .sellingPrice(entity.getSellingPrice())
        .pg(entity.getPg())
        .pgSellingPrice(entity.getPgSellingPrice())
        .description(entity.getDescription())
        .position(entity.getPosition())
        .status(entity.getStatus())
        .stockQuantity(entity.getStockQuantity())
        .stock(entity.getStock())
        .minimumStockLevel(entity.getMinimumStockLevel())
        .maximumStockLevel(entity.getMaximumStockLevel())
        .reviewCount(entity.getReviewCount())
        .reviewCountPg(entity.getReviewCountPg())
        .naverPartner(entity.getNaverPartner())
        .naverPartnerTitle(entity.getNaverPartnerTitle())
        .naverPartnerTitlePg(entity.getNaverPartnerTitlePg())
        .naverAttribute(entity.getNaverAttribute())
        .category(
            Optional.ofNullable(entity.getCategory()).map(categoryMapper::toModel).orElse(null))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .isRemoved(entity.isRemoved())
        .build();
  }

  public ProductEntity toEntity(Product model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<Product> toModelList(List<ProductEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public List<ProductEntity> toEntityList(List<Product> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
