package kr.co.pincoin.api.app.admin.product.controller;

import java.math.BigDecimal;
import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.ProductCreateRequest;
import kr.co.pincoin.api.app.admin.product.service.AdminProductService;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.global.response.model.ProductResponse;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {

  private final AdminProductService adminProductService;

  @PostMapping
  public ApiResponse<ProductResponse> createProduct(@RequestBody ProductCreateRequest request) {
    Product product = adminProductService.createProduct(request);
    return ApiResponse.of(
        ProductResponse.from(product), HttpStatus.CREATED, "Product created successfully");
  }

  @PutMapping("/{productId}/sold-out")
  public ApiResponse<ProductResponse> markAsSoldOut(@PathVariable Long productId) {
    Product product = adminProductService.markAsSoldOut(productId);
    return ApiResponse.of(ProductResponse.from(product), "Product marked as sold out");
  }

  @PutMapping("/{productId}/suspend")
  public ApiResponse<ProductResponse> suspendSale(@PathVariable Long productId) {
    Product product = adminProductService.suspendSale(productId);
    return ApiResponse.of(ProductResponse.from(product), "Product sale suspended");
  }

  @GetMapping("/category/{categoryId}")
  public ApiResponse<List<ProductResponse>> getProductsByCategory(@PathVariable Long categoryId) {
    List<Product> products = adminProductService.getProductsByCategory(categoryId, null,
        ProductStatus.ENABLED, ProductStock.IN_STOCK);
    List<ProductResponse> responses = products.stream().map(ProductResponse::from).toList();
    return ApiResponse.of(responses);
  }

  @PutMapping("/{productId}/price")
  public ApiResponse<ProductResponse> updatePrice(
      @PathVariable Long productId,
      @RequestParam BigDecimal listPrice,
      @RequestParam BigDecimal sellingPrice) {
    Product product = adminProductService.updatePrice(productId, listPrice, sellingPrice);
    return ApiResponse.of(ProductResponse.from(product), "Product price updated successfully");
  }

  @PutMapping("/{productId}/stock")
  public ApiResponse<ProductResponse> updateStockQuantity(
      @PathVariable Long productId, @RequestParam Integer stockQuantity) {
    Product product = adminProductService.updateStockQuantity(productId, stockQuantity);
    return ApiResponse.of(
        ProductResponse.from(product), "Product stock quantity updated successfully");
  }

  @PutMapping("/{productId}/category")
  public ApiResponse<ProductResponse> updateCategory(
      @PathVariable Long productId, @RequestParam Long newCategoryId) {
    Product product = adminProductService.updateCategory(productId, newCategoryId);
    return ApiResponse.of(ProductResponse.from(product), "Product category updated successfully");
  }
}
