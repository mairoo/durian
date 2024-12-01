package kr.co.pincoin.api.infra.shop.service;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.domain.shop.repository.product.CategoryRepository;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.domain.shop.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogPersistenceService {
  private final CategoryRepository categoryRepository;

  private final ProductRepository productRepository;

  private final StoreRepository storeRepository;

  // Store 관련 메서드
  public Optional<Store> findStoreById(Long id) {
    return storeRepository.findById(id);
  }

  // Category 관련 메서드
  @Transactional
  public Category saveCategory(Category category) {
    return categoryRepository.save(category);
  }

  public Optional<Category> findCategoryById(Long id) {
    return categoryRepository.findById(id);
  }

  public Optional<Category> findCategoryBySlug(String slug) {
    return categoryRepository.findBySlug(slug);
  }

  public boolean existsCategoryBySlug(String slug) {
    return categoryRepository.existsBySlug(slug);
  }

  public List<Category> findCategoriesByStoreId(Long storeId) {
    return categoryRepository.findAllByStoreIdWithStore(storeId);
  }

  public List<Category> findChildCategories(Category parentCategory) {
    return categoryRepository.findAllByParentCategory(parentCategory);
  }

  public List<Category> findRootCategoriesByStoreId(Long storeId) {
    return categoryRepository.findAllByStoreIdAndParentCategoryIsNull(storeId);
  }

  // Product 관련 메서드
  @Transactional
  public Product saveProduct(Product product) {
    return productRepository.save(product);
  }

  public Optional<Product> findProductById(Long id) {
    return productRepository.findById(id);
  }

  public Optional<Product> findProductWithCategory(Long id) {
    // Join fetch로 Category까지 한번에 조회
    return productRepository.findByIdWithCategory(id);
  }

  public boolean existsProductBySlug(String slug) {
    return productRepository.existsBySlug(slug);
  }

  public List<Product> findProductsByCategoryId(Long categoryId) {
    // JPQL로 변경하여 한 번의 쿼리로 처리
    return productRepository.findAllByCategoryIdWithCategory(categoryId);
  }
}
