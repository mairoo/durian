package kr.co.pincoin.api.app.admin.product.controller;

import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.CategoryCreateRequest;
import kr.co.pincoin.api.app.admin.product.service.AdminCategoryService;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.product.CategoryDetached;
import kr.co.pincoin.api.global.response.model.CategoryResponse;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
  private final AdminCategoryService adminCategoryService;

  /**
   * 새로운 카테고리를 생성합니다.
   *
   * @param request 카테고리 생성에 필요한 정보를 담은 요청 객체
   * @return 생성된 카테고리 정보를 포함한 ApiResponse
   */
  @PostMapping
  public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
      @RequestBody CategoryCreateRequest request) {
    Category category = adminCategoryService.createCategory(request);
    return ResponseEntity.ok(ApiResponse.of(CategoryResponse.from(category)));
  }

  /**
   * ID로 카테고리를 조회합니다.
   *
   * @param id 조회할 카테고리 ID
   * @return 카테고리 정보를 포함한 ApiResponse
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
    CategoryDetached category = adminCategoryService.getCategoryById(id);
    return ResponseEntity.ok(ApiResponse.of(CategoryResponse.from(category)));
  }

  /**
   * Slug로 카테고리를 조회합니다.
   *
   * @param slug 조회할 카테고리의 slug
   * @return 카테고리 정보를 포함한 ApiResponse
   */
  @GetMapping("/slug/{slug}")
  public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(
      @PathVariable String slug) {
    CategoryDetached category = adminCategoryService.getCategoryBySlug(slug);
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
    List<Category> categories = adminCategoryService.getCategoryListByStore(storeId);
    return ResponseEntity.ok(
        ApiResponse.of(categories.stream().map(CategoryResponse::from).toList()));
  }

  /**
   * 스토어의 최상위 카테고리 목록을 조회합니다.
   *
   * @param storeId 스토어 ID
   * @return 스토어의 최상위 카테고리 목록을 포함한 ApiResponse
   */
  @GetMapping("/root/{storeId}")
  public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories(
      @PathVariable Long storeId) {
    List<Category> categories = adminCategoryService.getRootCategories(storeId);
    return ResponseEntity.ok(
        ApiResponse.of(categories.stream().map(CategoryResponse::from).toList()));
  }
}
