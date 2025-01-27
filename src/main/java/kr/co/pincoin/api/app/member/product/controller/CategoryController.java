package kr.co.pincoin.api.app.member.product.controller;

import java.util.List;
import kr.co.pincoin.api.app.member.product.service.CategoryService;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.product.CategoryDetached;
import kr.co.pincoin.api.global.response.model.CategoryResponse;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
  private final CategoryService categoryService;

  /**
   * Slug로 카테고리를 조회합니다.
   *
   * @param slug 조회할 카테고리의 slug
   * @return 카테고리 정보를 포함한 ApiResponse
   */
  @GetMapping("/{slug}")
  public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(
      @PathVariable String slug) {
    CategoryDetached category = categoryService.getCategoryBySlug(slug);
    return ResponseEntity.ok(ApiResponse.of(CategoryResponse.from(category)));
  }

  /**
   * 스토어에 속한 모든 카테고리 목록을 조회합니다.
   *
   * @param storeId 스토어 ID
   * @return 스토어의 카테고리 목록을 포함한 ApiResponse
   */
  @GetMapping("/store/{storeId}")
  public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryListByStore(
      @PathVariable Long storeId) {
    List<Category> categories = categoryService.getCategoryListByStore(storeId);
    List<CategoryResponse> responses = categories.stream().map(CategoryResponse::from).toList();
    return ResponseEntity.ok(ApiResponse.of(responses));
  }

  /**
   * 스토어의 최상위(루트) 카테고리 목록을 조회합니다.
   *
   * @param storeId 스토어 ID
   * @return 스토어의 최상위 카테고리 목록을 포함한 ApiResponse
   */
  @GetMapping("/root/{storeId}")
  public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories(
      @PathVariable Long storeId) {
    List<Category> categories = categoryService.getRootCategories(storeId);
    List<CategoryResponse> responses = categories.stream().map(CategoryResponse::from).toList();
    return ResponseEntity.ok(ApiResponse.of(responses));
  }
}
