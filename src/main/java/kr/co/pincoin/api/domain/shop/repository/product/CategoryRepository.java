package kr.co.pincoin.api.domain.shop.repository.product;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Category;

public interface CategoryRepository {

  Category save(Category category);

  Optional<Category> findById(Long id);

  void delete(Category category);

  void deleteById(Long id);

  Optional<Category> findBySlug(String slug);

  List<Category> findAll();

  boolean existsById(Long id);

  boolean existsBySlug(String slug);
}
