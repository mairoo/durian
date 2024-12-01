package kr.co.pincoin.api.app.member.product.service;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

  private final CatalogService catalogService;

  public Category getCategoryById(Long id) {
    return catalogService.getCategoryById(id);
  }

  public Category getCategoryBySlug(String slug) {
    return catalogService.getCategoryBySlug(slug);
  }

  public List<Category> getCategoryListByStore(Long storeId) {
    return catalogService.getCategoryListByStore(storeId);
  }

  public List<Category> getChildCategories(Long parentId) {
    return catalogService.getChildCategories(parentId);
  }

  public List<Category> getRootCategories(Long storeId) {
    return catalogService.getRootCategories(storeId);
  }
}
