package kr.co.pincoin.api.domain.shop.service;

import java.math.BigDecimal;
import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.CategoryCreateRequest;
import kr.co.pincoin.api.app.admin.product.request.ProductCreateRequest;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.product.CategoryDetached;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.infra.shop.service.CatalogPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CatalogService {
  private final CatalogPersistenceService catalogPersistence;

  /**
   * 새로운 카테고리를 생성합니다.
   *
   * @param request 카테고리 생성에 필요한 정보를 담은 요청 객체
   * @return 생성된 카테고리 엔티티
   * @throws BusinessException 스토어를 찾을 수 없거나, 부모 카테고리를 찾을 수 없거나, 중복된 슬러그가 있는 경우 발생
   */
  @Transactional
  public Category createCategory(CategoryCreateRequest request) {
    Store store =
        catalogPersistence
            .findStoreById(request.getStoreId())
            .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

    Category parentCategory = null;

    if (request.getParentId() != null) {
      parentCategory =
          catalogPersistence
              .findCategoryById(request.getParentId())
              .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    if (catalogPersistence.findCategoryBySlug(request.getSlug()).isPresent()) {
      throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_SLUG);
    }

    Category category =
        Category.of(
            request.getTitle(),
            request.getSlug(),
            request.getDiscountRate(),
            request.getPg(),
            request.getPgDiscountRate(),
            parentCategory,
            store);

    return catalogPersistence.saveCategory(category);
  }

  /**
   * ID로 분리된(detached) 상태의 카테고리를 조회합니다.
   *
   * @param id 카테고리 ID
   * @return 분리된 상태의 카테고리 엔티티
   * @throws BusinessException 카테고리를 찾을 수 없는 경우 발생
   */
  public CategoryDetached getCategoryDetachedById(Long id) {
    return catalogPersistence
        .findCategoryDetachedById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
  }

  /**
   * 슬러그로 분리된(detached) 상태의 카테고리를 조회합니다.
   *
   * @param slug 카테고리 슬러그
   * @return 분리된 상태의 카테고리 엔티티
   * @throws BusinessException 카테고리를 찾을 수 없는 경우 발생
   */
  public CategoryDetached getCategoryDetachedBySlug(String slug) {
    return catalogPersistence
        .findCategoryDetachedBySlug(slug)
        .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
  }

  /**
   * 특정 스토어의 모든 카테고리를 조회합니다.
   *
   * @param storeId 스토어 ID
   * @return 카테고리 엔티티 목록
   */
  public List<Category> getCategoryListByStore(Long storeId) {
    return catalogPersistence.findCategoriesByStoreId(storeId);
  }

  /**
   * 특정 스토어의 모든 루트 카테고리(상위 카테고리가 없는 카테고리)를 조회합니다.
   *
   * @param storeId 스토어 ID
   * @return 루트 카테고리 엔티티 목록
   */
  public List<Category> getRootCategories(Long storeId) {
    return catalogPersistence.findRootCategoriesByStoreId(storeId);
  }

  // =============== 상품 CRUD 작업 ===============

  /**
   * 새로운 상품을 생성합니다.
   *
   * @param request 상품 생성에 필요한 정보를 담은 요청 객체
   * @return 생성된 상품 엔티티
   * @throws BusinessException 스토어를 찾을 수 없거나, 카테고리를 찾을 수 없거나, 중복된 상품 코드가 있는 경우 발생
   */
  @Transactional
  public Product createProduct(ProductCreateRequest request) {
    Store store =
        catalogPersistence
            .findStoreById(request.getStoreId())
            .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

    Category category =
        catalogPersistence
            .findCategoryById(request.getCategoryId())
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

    if (catalogPersistence.findProductByCode(request.getCode(), null, null).isPresent()) {
      throw new BusinessException(ErrorCode.DUPLICATE_PRODUCT_CODE);
    }

    Product product =
        Product.of(
            request.getName(),
            request.getSubtitle(),
            request.getCode(),
            request.getPg(),
            store,
            category,
            request.getListPrice(),
            request.getSellingPrice(),
            request.getPgSellingPrice(),
            request.getMinimumStockLevel(),
            request.getMaximumStockLevel());

    return catalogPersistence.saveProduct(product);
  }

  /**
   * ID로 상품을 조회합니다.
   *
   * @param productId 상품 ID
   * @param status 상품 상태 필터
   * @param stock 재고 상태 필터
   * @return 상품 엔티티
   * @throws BusinessException 상품을 찾을 수 없는 경우 발생
   */
  public Product getProductById(Long productId, ProductStatus status, ProductStock stock) {
    return catalogPersistence
        .findProductById(productId, status, stock)
        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
  }

  /**
   * ID로 분리된(detached) 상태의 상품을 조회합니다.
   *
   * @param productId 상품 ID
   * @param status 상품 상태 필터
   * @param stock 재고 상태 필터
   * @return 분리된 상태의 상품 엔티티
   * @throws BusinessException 상품을 찾을 수 없는 경우 발생
   */
  public ProductDetached getProductDetachedById(
      Long productId, ProductStatus status, ProductStock stock) {
    return catalogPersistence
        .findProductDetachedById(productId, status, stock)
        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
  }

  /**
   * 상품 코드로 상품을 조회합니다.
   *
   * @param code 상품 코드
   * @param status 상품 상태 필터
   * @param stock 재고 상태 필터
   * @return 분리된 상태의 상품 엔티티
   * @throws BusinessException 상품을 찾을 수 없는 경우 발생
   */
  public ProductDetached getProductByCode(String code, ProductStatus status, ProductStock stock) {
    return catalogPersistence
        .findProductByCode(code, status, stock)
        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
  }

  /**
   * 카테고리별 상품 목록을 조회합니다.
   *
   * @param categoryId 카테고리 ID
   * @param categorySlug 카테고리 슬러그
   * @param status 상품 상태 필터
   * @param stock 재고 상태 필터
   * @return 분리된 상태의 상품 엔티티 목록
   */
  public List<ProductDetached> getProductsByCategory(
      Long categoryId, String categorySlug, ProductStatus status, ProductStock stock) {
    return catalogPersistence.findProductsByCategory(categoryId, categorySlug, status, stock);
  }

  /**
   * 상품의 가격을 업데이트합니다.
   *
   * @param productId 상품 ID
   * @param listPrice 새로운 정가
   * @param sellingPrice 새로운 판매가
   * @return 업데이트된 상품 엔티티
   * @throws BusinessException 상품을 찾을 수 없는 경우 발생
   */
  @Transactional
  public Product updateProductPrice(Long productId, BigDecimal listPrice, BigDecimal sellingPrice) {
    Product product = getProductById(productId, ProductStatus.ENABLED, null);
    product.updatePrices(listPrice, sellingPrice);
    return catalogPersistence.saveProduct(product);
  }

  /**
   * 상품의 재고 수량을 업데이트합니다.
   *
   * @param productId 상품 ID
   * @param stockQuantity 새로운 재고 수량
   * @return 업데이트된 상품 엔티티
   * @throws BusinessException 상품을 찾을 수 없는 경우 발생
   */
  @Transactional
  public Product updateProductStockQuantity(Long productId, Integer stockQuantity) {
    Product product = getProductById(productId, ProductStatus.ENABLED, null);
    product.updateStockQuantity(stockQuantity);
    return catalogPersistence.saveProduct(product);
  }

  /**
   * 상품의 카테고리를 변경합니다.
   *
   * @param productId 상품 ID
   * @param newCategoryId 새로운 카테고리 ID
   * @return 업데이트된 상품 엔티티
   * @throws BusinessException 상품이나 새로운 카테고리를 찾을 수 없는 경우 발생
   */
  @Transactional
  public Product updateProductCategory(Long productId, Long newCategoryId) {
    Product product =
        catalogPersistence
            .findProductWithCategory(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

    Category newCategory =
        catalogPersistence
            .findCategoryById(newCategoryId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

    product.updateCategory(newCategory);
    return catalogPersistence.saveProduct(product);
  }

  /**
   * 상품을 품절 상태로 표시합니다. (재고를 0으로 설정)
   *
   * @param productId 상품 ID
   * @return 업데이트된 상품 엔티티
   * @throws BusinessException 상품을 찾을 수 없는 경우 발생
   */
  @Transactional
  public Product markProductAsSoldOut(Long productId) {
    Product product = getProductById(productId, ProductStatus.ENABLED, ProductStock.IN_STOCK);
    product.updateStockQuantity(0);
    return catalogPersistence.saveProduct(product);
  }

  /**
   * 상품 판매를 중지합니다. (상품 상태를 비활성화로 변경)
   *
   * @param productId 상품 ID
   * @return 업데이트된 상품 엔티티
   * @throws BusinessException 상품을 찾을 수 없는 경우 발생
   */
  @Transactional
  public Product suspendProductSale(Long productId) {
    Product product = getProductById(productId, ProductStatus.ENABLED, ProductStock.IN_STOCK);
    product.updateStatus(ProductStatus.DISABLED);
    return catalogPersistence.saveProduct(product);
  }
}
