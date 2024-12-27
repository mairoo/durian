package kr.co.pincoin.api.infra.shop.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.co.pincoin.api.app.member.order.request.CartItem;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.product.CategoryDetached;
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
  public Category saveCategory(Category category) {
    return categoryRepository.save(category);
  }

  public Optional<Category> findCategoryById(Long id) {
    return categoryRepository.findById(id);
  }

  public Optional<Category> findCategoryBySlug(String slug) {
    return categoryRepository.findBySlug(slug);
  }

  public Optional<CategoryDetached> findCategoryDetachedById(Long id) {
    return categoryRepository.findDetachedById(id);
  }

  public Optional<CategoryDetached> findCategoryDetachedBySlug(String slug) {
    return categoryRepository.findDetachedBySlug(slug);
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

  public Optional<ProductDetached> findProductDetachedById(
      Long id, ProductStatus status, ProductStock stock) {
    return productRepository.findDetachedById(id, status, stock);
  }

  public Optional<ProductDetached> findProductByCode(
      String code, ProductStatus status, ProductStock stock) {
    return productRepository.findDetachedByCode(code, status, stock);
  }

  public Optional<Product> findProductWithCategory(Long id) {
    return productRepository.findByIdWithCategory(id);
  }

  public List<ProductDetached> findProductsByCategory(
      Long categoryId, String categorySlug, ProductStatus status, ProductStock stock) {
    return productRepository.findAllByCategory(categoryId, categorySlug, status, stock);
  }

  /** 장바구니 아이템으로 상품 목록 조회 */
  public List<Product> findProductsByCartItems(List<CartItem> items) {
    return productRepository.findAllByCodeIn(
        items.stream().map(CartItem::getCode).distinct().toList());
  }

  /** 상품 코드 목록으로 상품 Map 조회 */
  public Map<String, Product> findProductsByCodeIn(List<String> codes) {
    return productRepository.findAllByCodeIn(codes).stream()
        .collect(Collectors.toMap(Product::getCode, Function.identity()));
  }

  /** 상품 코드 목록으로 분리된 상품 Map 조회 (활성화되고 재고있는 상품만) */
  public Map<String, ProductDetached> findProductsDetachedByCodeIn(List<String> codes) {
    return productRepository
        .findAllDetachedByCodeIn(codes, ProductStatus.ENABLED, ProductStock.IN_STOCK)
        .stream()
        .collect(Collectors.toMap(ProductDetached::getCode, Function.identity()));
  }

  /** 상품 업데이트 */
  @Transactional
  public void updateProduct(Product product) {
    productRepository.save(product);
  }

  /** 상품 목록 일괄 업데이트 */
  @Transactional
  public void updateProductsBatch(List<Product> products) {
    int batchSize = 100;
    for (int i = 0; i < products.size(); i += batchSize) {
      List<Product> batch = products.subList(i, Math.min(i + batchSize, products.size()));
      productRepository.saveAll(batch);
    }
  }
}
