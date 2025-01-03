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

        // - 1. 도메인 로직에서 명시적으로 setCategory(null)
        //  또는 DB에서 조회했을 때 category 컬럼이 null: entity.getCategory() null 반환
        // - 2. LAZY 로딩으로 설정된 경우: 프록시 객체이므로 entity.getCategory().getTitle() 등 조회 시점에 조회 쿼리
        // - 3. fetch join 조회한 경우: 이미 로딩된 엔티티를 추가 쿼리 없이 반환
        .category(
            Optional.ofNullable(entity.getCategory()) // 여기서 entity.getCategory()는 프록시 객체 반환
                .map(categoryMapper::toModel) // categoryMapper가 이 프록시 객체를 도메인 모델로 변환
                .orElse(null))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .isRemoved(entity.isRemoved())
        .build();
  }

  public List<Product> toModelList(List<ProductEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public ProductEntity toEntity(Product model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<ProductEntity> toEntityList(List<Product> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
