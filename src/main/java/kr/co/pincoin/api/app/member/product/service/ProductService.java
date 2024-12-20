package kr.co.pincoin.api.app.member.product.service;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.domain.shop.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

  private final CatalogService catalogService;

  public Product getProductById(Long id) {
    return catalogService.getProductById(id, ProductStatus.ENABLED, ProductStock.IN_STOCK);
  }

  public ProductDetached getProductDetachedById(Long id) {
    return catalogService.getProductDetachedById(id, ProductStatus.ENABLED, ProductStock.IN_STOCK);
  }

  public Product getProductByCode(String code) {
    return catalogService.getProductByCode(code);
  }

  public List<ProductDetached> getProductsByCategory(Long categoryId) {
    return catalogService.getProductsByCategory(categoryId, null, ProductStatus.ENABLED,
        ProductStock.IN_STOCK);
  }

  public List<ProductDetached> getProductsByCategorySlug(String categorySlug) {
    return catalogService.getProductsByCategory(null, categorySlug, ProductStatus.ENABLED,
        ProductStock.IN_STOCK);
  }
}
