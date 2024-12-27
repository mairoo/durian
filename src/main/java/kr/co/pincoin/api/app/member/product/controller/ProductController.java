package kr.co.pincoin.api.app.member.product.controller;

import java.util.List;
import kr.co.pincoin.api.app.member.product.response.ProductResponse;
import kr.co.pincoin.api.app.member.product.service.ProductService;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

  private final ProductService productService;

  @GetMapping("")
  public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) String categorySlug) throws BadRequestException {
    if (categoryId != null && categorySlug != null) {
      throw new BadRequestException("카테고리 ID와 이름을 동시에 사용할 수 없습니다");
    }

    if (categoryId == null && categorySlug == null) {
      throw new BadRequestException("카테고리 ID 또는 이름을 반드시 제공해야 합니다");
    }

    List<ProductDetached> products;

    if (categoryId != null) {
      products = productService.getProductsByCategory(categoryId);
    } else {
      products = productService.getProductsByCategorySlug(categorySlug);
    }

    List<ProductResponse> responses = products.stream()
        .map(ProductResponse::from)
        .toList();

    return ResponseEntity.ok(ApiResponse.of(responses));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
    ProductDetached product = productService.getProductDetachedById(id);
    return ResponseEntity.ok(ApiResponse.of(ProductResponse.from(product)));
  }

  @GetMapping("/code/{code}")
  public ResponseEntity<ApiResponse<ProductResponse>> getProductByCode(@PathVariable String code) {
    Product product = productService.getProductByCode(code);
    return ResponseEntity.ok(ApiResponse.of(ProductResponse.from(product)));
  }
}
