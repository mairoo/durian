package kr.co.pincoin.api.infra.shop.mapper.product;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.product.ProductListMembership;
import kr.co.pincoin.api.infra.shop.entity.product.ProductListMembershipEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductListMembershipMapper {
  private final ProductMapper productMapper;
  private final ProductListMapper productListMapper;

  public ProductListMembership toModel(ProductListMembershipEntity entity) {
    if (entity == null) {
      return null;
    }

    return ProductListMembership.builder()
        .id(entity.getId())
        .position(entity.getPosition())
        .product(productMapper.toModel(entity.getProduct()))
        .productList(productListMapper.toModel(entity.getProductList()))
        .build();
  }

  public ProductListMembershipEntity toEntity(ProductListMembership model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<ProductListMembership> toModelList(List<ProductListMembershipEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public List<ProductListMembershipEntity> toEntityList(List<ProductListMembership> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
