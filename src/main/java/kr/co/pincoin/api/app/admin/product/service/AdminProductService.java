package kr.co.pincoin.api.app.admin.product.service;

import kr.co.pincoin.api.app.admin.product.request.ProductCreateRequest;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.domain.shop.repository.product.CategoryRepository;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.domain.shop.repository.store.StoreRepository;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductService {
    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final StoreRepository storeRepository;


    /**
     * 새 상품권 권종을 등록한다. (디폴트 판매 개시)
     */
    @Transactional
    public Product
    createProduct(ProductCreateRequest request) {
        // Store 조회
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // Category 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 상품 코드 중복 검사
        if (productRepository.existsBySlug(request.getCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PRODUCT_CODE);
        }

        // Product 생성
        Product product = Product.of(
                request.getName(),
                request.getSubtitle(),
                request.getCode(),
                request.getListPrice(),
                request.getSellingPrice(),
                request.getPg(),
                request.getPgSellingPrice(),
                request.getMinimumStockLevel(),
                request.getMaximumStockLevel(),
                category,
                store);

        return productRepository.save(product);
    }

    /**
     * 상품권 품절 처리한다.
     */
    @Transactional
    public Product
    markAsSoldOut(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.updateStockQuantity(0);

        return productRepository.save(product);
    }

    /**
     * 상품권 판매 중지한다.
     */
    @Transactional
    public Product
    suspendSale(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        // TODO: Product 도메인에 상태 변경 메소드 추가 필요
        // product.updateStatus(ProductStatus.SUSPENDED);

        return productRepository.save(product);
    }

    /**
     * 특정 카테고리 안에 상품권을 모두 가져온다.
     */
    public List<Product>
    getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        return productRepository.findAll().stream()
                .filter(product -> product.getCategory().getId().equals(categoryId))
                .toList();
    }

    /**
     * 상품권의 가격을 변경한다.
     */
    @Transactional
    public Product updatePrice(Long productId, BigDecimal listPrice, BigDecimal sellingPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        // TODO: Product 도메인에 가격 변경 메소드 추가 필요
        // product.updatePrices(listPrice, sellingPrice);

        return productRepository.save(product);
    }

    /**
     * 상품권의 재고수량을 변경한다.
     */
    @Transactional
    public Product
    updateStockQuantity(Long productId, Integer stockQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.updateStockQuantity(stockQuantity);

        return productRepository.save(product);
    }

    /**
     * 상품권의 카테고리를 변경한다.
     */
    @Transactional
    public Product
    updateCategory(Long productId, Long newCategoryId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        Category newCategory = categoryRepository.findById(newCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // TODO: Product 도메인에 카테고리 변경 메소드 추가 필요
        // product.updateCategory(newCategory);

        return productRepository.save(product);
    }
}
