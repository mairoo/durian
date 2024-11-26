package kr.co.pincoin.api.domain.shop.repository.product;

import kr.co.pincoin.api.domain.shop.model.product.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);

    Optional<Category> findById(Long id);

    Optional<Category> findBySlug(String slug);

    List<Category> findAll();

    boolean existsBySlug(String slug);

    void delete(Category category);

    void deleteById(Long id);
}