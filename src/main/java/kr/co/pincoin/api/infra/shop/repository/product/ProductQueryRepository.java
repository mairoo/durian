package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.ProductList;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.infra.shop.entity.product.ProductEntity;

public interface ProductQueryRepository {

    Optional<ProductEntity> findById(Long id, ProductStatus status, ProductStock stock);

    List<ProductList> findAllByCategory(Long categoryId, String categorySlug,
        ProductStatus status, ProductStock stock);
}
