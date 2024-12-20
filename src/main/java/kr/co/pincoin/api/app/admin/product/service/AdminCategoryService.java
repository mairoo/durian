package kr.co.pincoin.api.app.admin.product.service;

import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.CategoryCreateRequest;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.product.CategoryDetached;
import kr.co.pincoin.api.domain.shop.service.CatalogService;
import kr.co.pincoin.api.global.security.annotation.SuperUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@SuperUser
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminCategoryService {

  private final CatalogService catalogService;

  @Transactional
  public Category createCategory(CategoryCreateRequest request) {
    return catalogService.createCategory(request);
  }

  public CategoryDetached getCategoryById(Long id) {
    return catalogService.getCategoryDetachedById(id);
  }

  public CategoryDetached getCategoryBySlug(String slug) {
    return catalogService.getCategoryDetachedBySlug(slug);
  }

  public List<Category> getCategoryListByStore(Long storeId) {
    return catalogService.getCategoryListByStore(storeId);
  }

  public List<Category> getRootCategories(Long storeId) {
    return catalogService.getRootCategories(storeId);
  }
}
