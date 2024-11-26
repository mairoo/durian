package kr.co.pincoin.api.app.admin.product.service;

import kr.co.pincoin.api.app.admin.product.request.CategoryCreateRequest;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.domain.shop.repository.product.CategoryRepository;
import kr.co.pincoin.api.domain.shop.repository.store.StoreRepository;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCategoryService {
    private final StoreRepository storeRepository;

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(CategoryCreateRequest request) {
        // 1. Store 조회
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // 2. Parent Category 조회 (있는 경우)
        Category parentCategory = Optional.ofNullable(request.getParentId())
                .flatMap(categoryRepository::findById)
                .orElse(null);

        // 3. Slug 중복 검사
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_SLUG);
        }

        // 4. Category 생성
        Category category = Category.of(
                request.getTitle(),
                request.getSlug(),
                request.getDiscountRate(),
                request.getPg(),
                request.getPgDiscountRate(),
                parentCategory,
                store);

        // 5. Category 저장
        return categoryRepository.save(category);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    public List<Category> getCategoryListByStore(Long storeId) {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getStore().getId().equals(storeId))
                .toList();
    }

    public List<Category> getChildCategories(Long parentId) {
        Category parentCategory = categoryRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        return categoryRepository.findAll().stream()
                .filter(category -> category.isDescendantOf(parentCategory))
                .toList();
    }

    public List<Category> getRootCategories(Long storeId) {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getStore().getId().equals(storeId))
                .filter(Category::isRoot)
                .toList();
    }
}
