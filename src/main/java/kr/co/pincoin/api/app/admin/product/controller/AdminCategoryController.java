package kr.co.pincoin.api.app.admin.product.controller;

import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.CategoryCreateRequest;
import kr.co.pincoin.api.global.response.model.CategoryResponse;
import kr.co.pincoin.api.app.admin.product.service.AdminCategoryService;
import kr.co.pincoin.api.domain.shop.model.product.Category;
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

  @PostMapping
  public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
      @RequestBody CategoryCreateRequest request) {
    Category category = adminCategoryService.createCategory(request);
    return ResponseEntity.ok(ApiResponse.of(CategoryResponse.from(category)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
    Category category = adminCategoryService.getCategoryById(id);
    return ResponseEntity.ok(ApiResponse.of(CategoryResponse.from(category)));
  }

  @GetMapping("/slug/{slug}")
  public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(
      @PathVariable String slug) {
    Category category = adminCategoryService.getCategoryBySlug(slug);
    return ResponseEntity.ok(ApiResponse.of(CategoryResponse.from(category)));
  }

  @GetMapping("/store/{storeId}")
  public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryListByStore(
      @PathVariable Long storeId) {
    List<Category> categories = adminCategoryService.getCategoryListByStore(storeId);
    return ResponseEntity.ok(
        ApiResponse.of(categories.stream().map(CategoryResponse::from).toList()));
  }

  @GetMapping("/children/{parentId}")
  public ResponseEntity<ApiResponse<List<CategoryResponse>>> getChildCategories(
      @PathVariable Long parentId) {
    List<Category> categories = adminCategoryService.getChildCategories(parentId);
    return ResponseEntity.ok(
        ApiResponse.of(categories.stream().map(CategoryResponse::from).toList()));
  }

  @GetMapping("/root/{storeId}")
  public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories(
      @PathVariable Long storeId) {
    List<Category> categories = adminCategoryService.getRootCategories(storeId);
    return ResponseEntity.ok(
        ApiResponse.of(categories.stream().map(CategoryResponse::from).toList()));
  }
}
