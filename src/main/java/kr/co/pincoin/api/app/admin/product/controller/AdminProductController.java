package kr.co.pincoin.api.app.admin.product.controller;

import java.math.BigDecimal;
import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.ProductCreateRequest;
import kr.co.pincoin.api.app.admin.product.service.AdminProductService;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.ProductList;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.app.admin.product.response.AdminProductResponse;
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
  public ApiResponse<AdminProductResponse> createProduct(@RequestBody ProductCreateRequest request) {
    Product product = adminProductService.createProduct(request);
    return ApiResponse.of(
        AdminProductResponse.from(product), HttpStatus.CREATED, "Product created successfully");
  }

  @PutMapping("/{productId}/sold-out")
  public ApiResponse<AdminProductResponse> markAsSoldOut(@PathVariable Long productId) {
    Product product = adminProductService.markAsSoldOut(productId);
    return ApiResponse.of(AdminProductResponse.from(product), "Product marked as sold out");
  }

  @PutMapping("/{productId}/suspend")
  public ApiResponse<AdminProductResponse> suspendSale(@PathVariable Long productId) {
    Product product = adminProductService.suspendSale(productId);
    return ApiResponse.of(AdminProductResponse.from(product), "Product sale suspended");
  }

  @GetMapping("/category/{categoryId}")
  public ApiResponse<List<AdminProductResponse>> getProductsByCategory(@PathVariable Long categoryId) {
    List<ProductList> products = adminProductService.getProductsByCategory(categoryId, null,
        ProductStatus.ENABLED, ProductStock.IN_STOCK);
    List<AdminProductResponse> responses = products.stream().map(AdminProductResponse::from).toList();
    return ApiResponse.of(responses);
  }

  @PutMapping("/{productId}/price")
  public ApiResponse<AdminProductResponse> updatePrice(
      @PathVariable Long productId,
      @RequestParam BigDecimal listPrice,
      @RequestParam BigDecimal sellingPrice) {
    Product product = adminProductService.updatePrice(productId, listPrice, sellingPrice);
    return ApiResponse.of(AdminProductResponse.from(product), "Product price updated successfully");
  }

  @PutMapping("/{productId}/stock")
  public ApiResponse<AdminProductResponse> updateStockQuantity(
      @PathVariable Long productId, @RequestParam Integer stockQuantity) {
    Product product = adminProductService.updateStockQuantity(productId, stockQuantity);
    return ApiResponse.of(
        AdminProductResponse.from(product), "Product stock quantity updated successfully");
  }

  @PutMapping("/{productId}/category")
  public ApiResponse<AdminProductResponse> updateCategory(
      @PathVariable Long productId, @RequestParam Long newCategoryId) {
    Product product = adminProductService.updateCategory(productId, newCategoryId);
    return ApiResponse.of(AdminProductResponse.from(product), "Product category updated successfully");
  }
}
