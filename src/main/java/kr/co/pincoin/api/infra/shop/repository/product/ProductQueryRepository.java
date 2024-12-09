package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.infra.shop.entity.product.ProductEntity;

public interface ProductQueryRepository {

    List<ProductEntity> findAllByCategory(Long categoryId, String categorySlug,
        ProductStatus status, ProductStock stock);
}
