package kr.co.pincoin.api.app.admin.product.controller;

import java.math.BigDecimal;
import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.ProductCreateRequest;
import kr.co.pincoin.api.app.admin.product.response.AdminProductResponse;
import kr.co.pincoin.api.app.admin.product.service.AdminProductService;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  /**
   * 새로운 상품을 생성합니다.
   *
   * @param request 상품 생성에 필요한 정보를 담은 요청 객체
   * @return 생성된 상품 정보를 포함한 ApiResponse
   */
  @PostMapping
  public ResponseEntity<ApiResponse<AdminProductResponse>> createProduct(
      @RequestBody ProductCreateRequest request) {
    Product product = adminProductService.createProduct(request);
    return ResponseEntity.ok(
        ApiResponse.of(
            AdminProductResponse.from(product),
            HttpStatus.CREATED,
            "Product created successfully"));
  }

  /**
   * ID로 상품을 조회합니다.
   *
   * @param id 조회할 상품 ID
   * @return 상품 정보를 포함한 ApiResponse
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AdminProductResponse>> getProductByIdentifier(
      @PathVariable Long id) {
    ProductDetached product = adminProductService.getProductDetachedById(id);
    return ResponseEntity.ok(ApiResponse.of(AdminProductResponse.from(product)));
  }

  /**
   * 상품을 품절 상태로 변경합니다.
   *
   * @param productId 품절 처리할 상품 ID
   * @return 업데이트된 상품 정보를 포함한 ApiResponse
   */
  @PutMapping("/{productId}/sold-out")
  public ResponseEntity<ApiResponse<AdminProductResponse>> markAsSoldOut(
      @PathVariable Long productId) {
    Product product = adminProductService.markAsSoldOut(productId);
    return ResponseEntity.ok(
        ApiResponse.of(AdminProductResponse.from(product), "Product marked as sold out"));
  }

  /**
   * 상품 판매를 일시 중지합니다.
   *
   * @param productId 판매 중지할 상품 ID
   * @return 업데이트된 상품 정보를 포함한 ApiResponse
   */
  @PutMapping("/{productId}/suspend")
  public ResponseEntity<ApiResponse<AdminProductResponse>> suspendSale(
      @PathVariable Long productId) {
    Product product = adminProductService.suspendSale(productId);
    return ResponseEntity.ok(
        ApiResponse.of(AdminProductResponse.from(product), "Product sale suspended"));
  }

  /**
   * 카테고리에 속한 상품 목록을 조회합니다. 활성화 상태이며 재고가 있는 상품만 조회됩니다.
   *
   * @param categoryId 조회할 카테고리 ID
   * @return 상품 목록을 포함한 ApiResponse
   */
  @GetMapping("/category/{categoryId}")
  public ResponseEntity<ApiResponse<List<AdminProductResponse>>> getProductsByCategory(
      @PathVariable Long categoryId) {
    List<ProductDetached> products =
        adminProductService.getProductsByCategory(
            categoryId, null, ProductStatus.ENABLED, ProductStock.IN_STOCK);
    List<AdminProductResponse> responses =
        products.stream().map(AdminProductResponse::from).toList();
    return ResponseEntity.ok(ApiResponse.of(responses));
  }

  /**
   * 상품의 정가와 판매가를 업데이트합니다.
   *
   * @param productId 가격을 수정할 상품 ID
   * @param listPrice 수정할 정가
   * @param sellingPrice 수정할 판매가
   * @return 업데이트된 상품 정보를 포함한 ApiResponse
   */
  @PutMapping("/{productId}/price")
  public ResponseEntity<ApiResponse<AdminProductResponse>> updatePrice(
      @PathVariable Long productId,
      @RequestParam BigDecimal listPrice,
      @RequestParam BigDecimal sellingPrice) {
    Product product = adminProductService.updatePrice(productId, listPrice, sellingPrice);
    return ResponseEntity.ok(
        ApiResponse.of(AdminProductResponse.from(product), "Product price updated successfully"));
  }

  /**
   * 상품의 재고 수량을 업데이트합니다.
   *
   * @param productId 재고를 수정할 상품 ID
   * @param stockQuantity 수정할 재고 수량
   * @return 업데이트된 상품 정보를 포함한 ApiResponse
   */
  @PutMapping("/{productId}/stock")
  public ResponseEntity<ApiResponse<AdminProductResponse>> updateStockQuantity(
      @PathVariable Long productId, @RequestParam Integer stockQuantity) {
    Product product = adminProductService.updateStockQuantity(productId, stockQuantity);
    return ResponseEntity.ok(
        ApiResponse.of(
            AdminProductResponse.from(product), "Product stock quantity updated successfully"));
  }

  /**
   * 상품의 카테고리를 변경합니다.
   *
   * @param productId 카테고리를 변경할 상품 ID
   * @param newCategoryId 변경할 새 카테고리 ID
   * @return 업데이트된 상품 정보를 포함한 ApiResponse
   */
  @PutMapping("/{productId}/category")
  public ResponseEntity<ApiResponse<AdminProductResponse>> updateCategory(
      @PathVariable Long productId, @RequestParam Long newCategoryId) {
    Product product = adminProductService.updateCategory(productId, newCategoryId);
    return ResponseEntity.ok(
        ApiResponse.of(
            AdminProductResponse.from(product), "Product category updated successfully"));
  }
}
