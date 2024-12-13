package kr.co.pincoin.api.infra.shop.service;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.domain.shop.repository.product.CategoryRepository;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.domain.shop.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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

  public Optional<Product> findProductById(Long id, ProductStatus status, ProductStock stock) {
    return productRepository.findById(id, status, stock);
  }

  public Optional<ProductDetached> findProductDetachedById(Long id, ProductStatus status,
      ProductStock stock) {
    return productRepository.findDetachedById(id, status, stock);
  }

  public Optional<Product> findProductByCode(String code) {
    return productRepository.findByCode(code);
  }

  public Optional<Product> findProductWithCategory(Long id) {
    return productRepository.findByIdWithCategory(id);
  }

  public List<ProductDetached> findProductsByCategory(Long categoryId, String categorySlug,
      ProductStatus status, ProductStock stock) {
    return productRepository.findAllByCategory(categoryId, categorySlug, status, stock);
  }
}
