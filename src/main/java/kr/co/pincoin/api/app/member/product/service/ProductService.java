package kr.co.pincoin.api.app.member.product.service;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.product.Product;
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
    return catalogService.getProductById(id);
  }

  public Product getProductByCode(String code) {
    return catalogService.getProductByCode(code);
  }

  public List<Product> getProductsByCategory(Long categoryId) {
    return catalogService.getProductsByCategory(categoryId);
  }

  public List<Product> getProductsByCategorySlug(String categorySlug) {
    return catalogService.getProductsByCategorySlug(categorySlug);
  }

  public List<Product> getAvailableProductsByCategory(Long categoryId) {
    return catalogService.getProductsByCategory(categoryId).stream()
        .filter(product -> product.getStatus() == ProductStatus.ENABLED)
        .filter(product -> product.getStock() == ProductStock.IN_STOCK)
        .toList();
  }
}
