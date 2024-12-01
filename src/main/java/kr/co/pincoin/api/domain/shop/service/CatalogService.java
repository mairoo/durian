package kr.co.pincoin.api.domain.shop.service;

import java.math.BigDecimal;
import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.CategoryCreateRequest;
import kr.co.pincoin.api.app.admin.product.request.ProductCreateRequest;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.infra.shop.service.CatalogPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CatalogService {

    private final CatalogPersistenceService catalogPersistence;

    @Transactional
    public Category createCategory(CategoryCreateRequest request) {
        // 1. Store 존재 여부 확인
        if (!catalogPersistence.existsStoreById(request.getStoreId())) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }

        // 2. Slug 중복 검사
        if (catalogPersistence.existsCategoryBySlug(request.getSlug())) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_SLUG);
        }

        // 3. Parent Category 조회 (있는 경우)
        Category parentCategory = null;
        if (request.getParentId() != null) {
            parentCategory = catalogPersistence.findCategoryById(request.getParentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        // 4. Store 조회
        Store store = catalogPersistence.findStoreById(request.getStoreId())
            .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // 5. Category 생성
        Category category = Category.of(
            request.getTitle(),
            request.getSlug(),
            request.getDiscountRate(),
            request.getPg(),
            request.getPgDiscountRate(),
            parentCategory,
            store
        );

        return catalogPersistence.saveCategory(category);
    }

    public Category getCategoryById(Long id) {
        return catalogPersistence.findCategoryById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    public Category getCategoryBySlug(String slug) {
        return catalogPersistence.findCategoryBySlug(slug)
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    public List<Category> getCategoryListByStore(Long storeId) {
        return catalogPersistence.findCategoriesByStoreId(storeId);
    }

    public List<Category> getChildCategories(Long parentId) {
        Category parentCategory = getCategoryById(parentId);
        return catalogPersistence.findChildCategories(parentCategory);
    }

    public List<Category> getRootCategories(Long storeId) {
        return catalogPersistence.findRootCategoriesByStoreId(storeId);
    }


    @Transactional
    public Product createProduct(ProductCreateRequest request) {
        // 1. Store 조회
        Store store = catalogPersistence.findStoreById(request.getStoreId())
            .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // 2. Category 조회
        Category category = catalogPersistence.findCategoryById(request.getCategoryId())
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 3. 상품 코드 중복 검사
        if (catalogPersistence.existsProductBySlug(request.getCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PRODUCT_CODE);
        }

        // 4. Product 생성
        Product product = Product.of(
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
            request.getMaximumStockLevel()
        );

        return catalogPersistence.saveProduct(product);
    }

    @Transactional
    public Product markProductAsSoldOut(Long productId) {
        Product product = getProductById(productId);
        product.updateStockQuantity(0);
        return catalogPersistence.saveProduct(product);
    }

    @Transactional
    public Product suspendProductSale(Long productId) {
        Product product = getProductById(productId);
        product.updateStatus(ProductStatus.DISABLED);
        return catalogPersistence.saveProduct(product);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return catalogPersistence.findProductsByCategoryId(categoryId);
    }

    @Transactional
    public Product updateProductPrice(Long productId, BigDecimal listPrice,
        BigDecimal sellingPrice) {
        Product product = getProductById(productId);
        product.updatePrices(listPrice, sellingPrice);
        return catalogPersistence.saveProduct(product);
    }

    @Transactional
    public Product updateProductStockQuantity(Long productId, Integer stockQuantity) {
        Product product = getProductById(productId);
        product.updateStockQuantity(stockQuantity);
        return catalogPersistence.saveProduct(product);
    }

    @Transactional
    public Product updateProductCategory(Long productId, Long newCategoryId) {
        Product product = getProductById(productId);
        Category newCategory = getCategoryById(newCategoryId);

        product.updateCategory(newCategory);
        return catalogPersistence.saveProduct(product);
    }

    private Product getProductById(Long productId) {
        return catalogPersistence.findProductById(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}