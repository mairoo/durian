package kr.co.pincoin.api.app.member.product.controller;

import java.util.List;
import kr.co.pincoin.api.global.response.model.ProductResponse;
import kr.co.pincoin.api.app.member.product.service.ProductService;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

  private final ProductService productService;

  @GetMapping("/{id}")
  public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
    Product product = productService.getProductById(id);
    return ApiResponse.of(ProductResponse.from(product));
  }

  @GetMapping("/code/{code}")
  public ApiResponse<ProductResponse> getProductByCode(@PathVariable String code) {
    Product product = productService.getProductByCode(code);
    return ApiResponse.of(ProductResponse.from(product));
  }

  @GetMapping("/category/{categoryId}")
  public ApiResponse<List<ProductResponse>> getProductsByCategory(@PathVariable Long categoryId) {
    List<Product> products = productService.getProductsByCategory(categoryId);
    List<ProductResponse> responses = products.stream().map(ProductResponse::from).toList();
    return ApiResponse.of(responses);
  }

  @GetMapping("/category/{categoryId}/available")
  public ApiResponse<List<ProductResponse>> getAvailableProductsByCategory(
      @PathVariable Long categoryId) {
    List<Product> products = productService.getAvailableProductsByCategory(categoryId);
    List<ProductResponse> responses = products.stream().map(ProductResponse::from).toList();
    return ApiResponse.of(responses);
  }
}
