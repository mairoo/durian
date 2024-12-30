package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.CategoryDetached;

public interface CategoryQueryRepository {

  Optional<CategoryDetached> findDetachedById(Long id);

  Optional<CategoryDetached> findDetachedBySlug(String slug);
}
