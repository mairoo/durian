package kr.co.pincoin.api.app.admin.user.controller;

import jakarta.validation.Valid;
import kr.co.pincoin.api.app.admin.user.response.AdminUserResponse;
import kr.co.pincoin.api.app.admin.user.service.AdminProfileService;
import kr.co.pincoin.api.app.member.user.request.PasswordUpdateRequest;
import kr.co.pincoin.api.app.member.user.request.UserCreateRequest;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/profiles")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminProfileController {
  private final AdminProfileService adminProfileService;

  @PostMapping
  public ResponseEntity<ApiResponse<AdminUserResponse>> createUser(
      @Valid @RequestBody UserCreateRequest request) {
    Profile profile =
        adminProfileService.createUser(
            request.getEmail(),
            request.getPassword(),
            request.getUsername(),
            request.getFirstName(),
            request.getLastName());
    return ResponseEntity.ok().body(ApiResponse.of(AdminUserResponse.from(profile)));
  }

  @PostMapping("/admin")
  public ResponseEntity<ApiResponse<AdminUserResponse>> createAdmin(
      @Valid @RequestBody UserCreateRequest request) {
    Profile profile =
        adminProfileService.createAdmin(
            request.getEmail(),
            request.getPassword(),
            request.getUsername(),
            request.getFirstName(),
            request.getLastName());
    return ResponseEntity.ok().body(ApiResponse.of(AdminUserResponse.from(profile)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> getAllProfiles(Pageable pageable) {
    Page<Profile> profiles = adminProfileService.findProfiles(pageable);
    Page<AdminUserResponse> responses = profiles.map(AdminUserResponse::from);
    return ResponseEntity.ok().body(ApiResponse.of(responses));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<AdminUserResponse>> getProfile(@PathVariable Integer userId) {
    Profile profile = adminProfileService.findProfile(userId);
    return ResponseEntity.ok().body(ApiResponse.of(AdminUserResponse.from(profile)));
  }

  @PatchMapping("/{userId}/password/reset")
  public ResponseEntity<ApiResponse<AdminUserResponse>> resetPassword(
      @PathVariable Integer userId, @Valid @RequestBody PasswordUpdateRequest request) {
    Profile profile = adminProfileService.resetPassword(userId, request.getNewPassword());
    return ResponseEntity.ok().body(ApiResponse.of(AdminUserResponse.from(profile)));
  }

  @PatchMapping("/{userId}/withdrawal")
  public ResponseEntity<Void> forceWithdrawUser(@PathVariable Integer userId) {
    adminProfileService.forceWithdrawUser(userId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{userId}/admin-grant")
  public ResponseEntity<ApiResponse<AdminUserResponse>> grantAdminPrivileges(
      @PathVariable Integer userId) {
    Profile profile = adminProfileService.grantAdminPrivileges(userId);
    return ResponseEntity.ok().body(ApiResponse.of(AdminUserResponse.from(profile)));
  }

  @PatchMapping("/{userId}/admin-revoke")
  public ResponseEntity<ApiResponse<AdminUserResponse>> revokeAdminPrivileges(
      @PathVariable Integer userId) {
    Profile profile = adminProfileService.revokeAdminPrivileges(userId);
    return ResponseEntity.ok().body(ApiResponse.of(AdminUserResponse.from(profile)));
  }
}
