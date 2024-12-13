package kr.co.pincoin.api.app.admin.product.service;

import java.math.BigDecimal;
import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.ProductCreateRequest;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.domain.shop.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductService {

  private final CatalogService catalogService;

  @Transactional
  public Product createProduct(ProductCreateRequest request) {
    return catalogService.createProduct(request);
  }

  @Transactional
  public Product markAsSoldOut(Long productId) {
    return catalogService.markProductAsSoldOut(productId);
  }

  @Transactional
  public Product suspendSale(Long productId) {
    return catalogService.suspendProductSale(productId);
  }

  public List<ProductDetached> getProductsByCategory(Long categoryId, String categorySlug,
      ProductStatus status, ProductStock stock) {
    return catalogService.getProductsByCategory(categoryId, categorySlug, status, stock);
  }

  @Transactional
  public Product updatePrice(Long productId, BigDecimal listPrice, BigDecimal sellingPrice) {
    return catalogService.updateProductPrice(productId, listPrice, sellingPrice);
  }

  @Transactional
  public Product updateStockQuantity(Long productId, Integer stockQuantity) {
    return catalogService.updateProductStockQuantity(productId, stockQuantity);
  }

  @Transactional
  public Product updateCategory(Long productId, Long newCategoryId) {
    return catalogService.updateProductCategory(productId, newCategoryId);
  }
}
