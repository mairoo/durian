package kr.co.pincoin.api.app.member.product.controller;

import java.util.List;
import kr.co.pincoin.api.app.member.product.response.ProductResponse;
import kr.co.pincoin.api.app.member.product.service.ProductService;
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

  /**
   * 카테고리별 상품 목록을 조회합니다. 카테고리 ID 또는 Slug 중 하나만 사용하여 조회할 수 있습니다.
   *
   * @param categoryId 조회할 카테고리 ID (선택)
   * @param categorySlug 조회할 카테고리 Slug (선택)
   * @return 상품 목록을 포함한 ApiResponse
   * @throws BadRequestException 카테고리 ID와 Slug를 동시에 사용하거나, 둘 다 제공하지 않은 경우
   */
  @GetMapping("")
  public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) String categorySlug)
      throws BadRequestException {
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

    List<ProductResponse> responses = products.stream().map(ProductResponse::from).toList();

    return ResponseEntity.ok(ApiResponse.of(responses));
  }

  /**
   * 상품 코드로 상품을 조회합니다.
   *
   * @param code 조회할 상품 코드
   * @return 상품 정보를 포함한 ApiResponse
   */
  @GetMapping("/{code}")
  public ResponseEntity<ApiResponse<ProductResponse>> getProductByCode(@PathVariable String code) {
    ProductDetached product = productService.getProductByCode(code);
    return ResponseEntity.ok(ApiResponse.of(ProductResponse.from(product)));
  }
}
