package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.infra.shop.entity.product.ProductEntity;

public interface ProductQueryRepository {

  Optional<ProductEntity> findById(Long id, ProductStatus status, ProductStock stock);

  Optional<ProductDetached> findDetachedById(Long id, ProductStatus status, ProductStock stock);

  Optional<ProductDetached> findDetachedByCode(
      String code, ProductStatus status, ProductStock stock);

  List<ProductDetached> findAllByCategory(
      Long categoryId, String categorySlug, ProductStatus status, ProductStock stock);

  List<ProductDetached> findAllDetachedByCodeIn(
      Collection<String> codes, ProductStatus status, ProductStock stock);
}
