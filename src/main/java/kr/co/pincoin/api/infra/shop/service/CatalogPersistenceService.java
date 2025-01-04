package kr.co.pincoin.api.infra.shop.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
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

  /**
   * 스토어 ID로 스토어 정보를 조회합니다.
   *
   * @param id 스토어 ID
   * @return 스토어 정보
   */
  public Optional<Store> findStoreById(Long id) {
    return storeRepository.findById(id);
  }

  /**
   * 새로운 카테고리를 생성하거나 기존 카테고리를 업데이트합니다.
   *
   * @param category 저장할 카테고리 정보
   * @return 저장된 카테고리
   */
  public Category saveCategory(Category category) {
    return categoryRepository.save(category);
  }

  /**
   * 카테고리 ID로 카테고리를 조회합니다.
   *
   * @param id 카테고리 ID
   * @return 카테고리 정보
   */
  public Optional<Category> findCategoryById(Long id) {
    return categoryRepository.findById(id);
  }

  /**
   * 슬러그로 카테고리를 조회합니다.
   *
   * @param slug 카테고리 슬러그
   * @return 카테고리 정보
   */
  public Optional<Category> findCategoryBySlug(String slug) {
    return categoryRepository.findBySlug(slug);
  }

  /**
   * 카테고리 ID로 분리된 카테고리 정보를 조회합니다.
   *
   * @param id 카테고리 ID
   * @return 분리된 카테고리 정보
   */
  public Optional<CategoryDetached> findCategoryDetachedById(Long id) {
    return categoryRepository.findDetachedById(id);
  }

  /**
   * 슬러그로 분리된 카테고리 정보를 조회합니다.
   *
   * @param slug 카테고리 슬러그
   * @return 분리된 카테고리 정보
   */
  public Optional<CategoryDetached> findCategoryDetachedBySlug(String slug) {
    return categoryRepository.findDetachedBySlug(slug);
  }

  /**
   * 스토어 ID로 모든 카테고리를 조회합니다.
   *
   * @param storeId 스토어 ID
   * @return 카테고리 목록
   */
  public List<Category> findCategoriesByStoreId(Long storeId) {
    return categoryRepository.findAllByStoreIdWithStore(storeId);
  }

  /**
   * 부모 카테고리에 속한 하위 카테고리들을 조회합니다.
   *
   * @param parentCategory 부모 카테고리
   * @return 하위 카테고리 목록
   */
  public List<Category> findChildCategories(Category parentCategory) {
    return categoryRepository.findAllByParentCategory(parentCategory);
  }

  /**
   * 스토어 ID로 최상위 카테고리들을 조회합니다.
   *
   * @param storeId 스토어 ID
   * @return 최상위 카테고리 목록
   */
  public List<Category> findRootCategoriesByStoreId(Long storeId) {
    return categoryRepository.findAllByStoreIdAndParentCategoryIsNull(storeId);
  }

  // Product 관련 메서드 - CRUD 순서
  /**
   * 새로운 상품을 생성하거나 기존 상품을 업데이트합니다.
   *
   * @param product 저장할 상품 정보
   * @return 저장된 상품
   */
  @Transactional
  public Product saveProduct(Product product) {
    return productRepository.save(product);
  }

  /**
   * 상품 ID와 상태, 재고 상태로 상품을 조회합니다.
   *
   * @param id 상품 ID
   * @param status 상품 상태
   * @param stock 재고 상태
   * @return 상품 정보
   */
  public Optional<Product> findProductById(Long id, ProductStatus status, ProductStock stock) {
    return productRepository.findById(id, status, stock);
  }

  /**
   * 상품 ID와 상태, 재고 상태로 분리된 상품 정보를 조회합니다.
   *
   * @param id 상품 ID
   * @param status 상품 상태
   * @param stock 재고 상태
   * @return 분리된 상품 정보
   */
  public Optional<ProductDetached> findProductDetachedById(
      Long id, ProductStatus status, ProductStock stock) {
    return productRepository.findDetachedById(id, status, stock);
  }

  /**
   * 상품 코드와 상태, 재고 상태로 분리된 상품 정보를 조회합니다.
   *
   * @param code 상품 코드
   * @param status 상품 상태
   * @param stock 재고 상태
   * @return 분리된 상품 정보
   */
  public Optional<ProductDetached> findProductByCode(
      String code, ProductStatus status, ProductStock stock) {
    return productRepository.findDetachedByCode(code, status, stock);
  }

  /**
   * 상품 ID로 카테고리 정보를 포함한 상품을 조회합니다.
   *
   * @param id 상품 ID
   * @return 카테고리 정보가 포함된 상품
   */
  public Optional<Product> findProductWithCategory(Long id) {
    return productRepository.findByIdWithCategory(id);
  }

  /**
   * 카테고리 정보로 상품 목록을 조회합니다.
   *
   * @param categoryId 카테고리 ID
   * @param categorySlug 카테고리 슬러그
   * @param status 상품 상태
   * @param stock 재고 상태
   * @return 분리된 상품 목록
   */
  public List<ProductDetached> findProductsByCategory(
      Long categoryId, String categorySlug, ProductStatus status, ProductStock stock) {
    return productRepository.findAllByCategory(categoryId, categorySlug, status, stock);
  }

  /**
   * 상품 코드 목록으로 활성화되고 재고가 있는 상품들을 Map으로 조회합니다.
   *
   * @param codes 상품 코드 목록
   * @return 상품 코드를 키로 하는 상품 Map
   */
  public Map<String, ProductDetached> findProductsDetachedByCodeIn(List<String> codes) {
    return productRepository
        .findAllDetachedByCodeIn(codes, ProductStatus.ENABLED, ProductStock.IN_STOCK)
        .stream()
        .collect(Collectors.toMap(ProductDetached::getCode, Function.identity()));
  }

  /**
   * 상품 정보를 업데이트합니다.
   *
   * @param product 업데이트할 상품 정보
   */
  @Transactional
  public void updateProduct(Product product) {
    productRepository.save(product);
  }
}
