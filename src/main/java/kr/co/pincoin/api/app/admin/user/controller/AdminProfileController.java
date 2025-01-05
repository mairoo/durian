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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/profiles")
@RequiredArgsConstructor
@Slf4j
public class AdminProfileController {
  private final AdminProfileService adminProfileService;

  /**
   * 일반 사용자 계정을 생성합니다.
   *
   * @param request 사용자 생성에 필요한 정보를 담은 요청 객체 (이메일, 비밀번호, 사용자명, 이름)
   * @return 생성된 사용자 프로필 정보를 포함한 ApiResponse
   */
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

  /**
   * 관리자 계정을 생성합니다.
   *
   * @param request 관리자 생성에 필요한 정보를 담은 요청 객체 (이메일, 비밀번호, 사용자명, 이름)
   * @return 생성된 관리자 프로필 정보를 포함한 ApiResponse
   */
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

  /**
   * 모든 사용자 프로필을 페이지네이션하여 조회합니다.
   *
   * @param pageable 페이지네이션 정보
   * @return 사용자 프로필 목록과 페이지네이션 정보를 포함한 ApiResponse
   */
  @GetMapping
  public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> getAllProfiles(Pageable pageable) {
    Page<Profile> profiles = adminProfileService.findProfiles(pageable);
    Page<AdminUserResponse> responses = profiles.map(AdminUserResponse::from);
    return ResponseEntity.ok().body(ApiResponse.of(responses));
  }

  /**
   * 특정 사용자의 프로필을 조회합니다.
   *
   * @param userId 조회할 사용자 ID
   * @return 사용자 프로필 정보를 포함한 ApiResponse
   */
  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<AdminUserResponse>> getProfile(@PathVariable Integer userId) {
    Profile profile = adminProfileService.findProfile(userId);
    return ResponseEntity.ok().body(ApiResponse.of(AdminUserResponse.from(profile)));
  }

  /**
   * 사용자의 비밀번호를 재설정합니다.
   *
   * @param userId 비밀번호를 재설정할 사용자 ID
   * @param request 새로운 비밀번호 정보를 담은 요청 객체
   * @return 업데이트된 사용자 프로필 정보를 포함한 ApiResponse
   */
  @PatchMapping("/{userId}/password/reset")
  public ResponseEntity<ApiResponse<AdminUserResponse>> resetPassword(
      @PathVariable Integer userId, @Valid @RequestBody PasswordUpdateRequest request) {
    Profile profile = adminProfileService.resetPassword(userId, request.getNewPassword());
    return ResponseEntity.ok().body(ApiResponse.of(AdminUserResponse.from(profile)));
  }

  /**
   * 사용자 계정을 강제로 탈퇴 처리합니다.
   *
   * @param userId 탈퇴 처리할 사용자 ID
   * @return 204 No Content 응답
   */
  @PatchMapping("/{userId}/withdrawal")
  public ResponseEntity<Void> forceWithdrawUser(@PathVariable Integer userId) {
    adminProfileService.forceWithdrawUser(userId);
    return ResponseEntity.noContent().build();
  }

  /**
   * 사용자에게 관리자 권한을 부여합니다.
   *
   * @param userId 관리자 권한을 부여할 사용자 ID
   * @return 업데이트된 사용자 프로필 정보를 포함한 ApiResponse
   */
  @PatchMapping("/{userId}/admin-grant")
  public ResponseEntity<ApiResponse<AdminUserResponse>> grantAdminPrivileges(
      @PathVariable Integer userId) {
    Profile profile = adminProfileService.grantAdminPrivileges(userId);
    return ResponseEntity.ok().body(ApiResponse.of(AdminUserResponse.from(profile)));
  }

  /**
   * 사용자의 관리자 권한을 회수합니다.
   *
   * @param userId 관리자 권한을 회수할 사용자 ID
   * @return 업데이트된 사용자 프로필 정보를 포함한 ApiResponse
   */
  @PatchMapping("/{userId}/admin-revoke")
  public ResponseEntity<ApiResponse<AdminUserResponse>> revokeAdminPrivileges(
      @PathVariable Integer userId) {
    Profile profile = adminProfileService.revokeAdminPrivileges(userId);
    return ResponseEntity.ok().body(ApiResponse.of(AdminUserResponse.from(profile)));
  }
}
