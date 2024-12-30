package kr.co.pincoin.api.domain.shop.repository.product;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.product.CategoryDetached;

public interface CategoryRepository {

  Category save(Category category);

  Optional<Category> findById(Long id);

  Optional<Category> findBySlug(String slug);

  Optional<CategoryDetached> findDetachedById(Long id);

  Optional<CategoryDetached> findDetachedBySlug(String slug);

  List<Category> findAll();

  List<Category> findAllByStoreIdWithStore(Long storeId);

  List<Category> findAllByParentCategory(Category parent);

  List<Category> findAllByStoreIdAndParentCategoryIsNull(Long storeId);

  void delete(Category category);

  void deleteById(Long id);
}
