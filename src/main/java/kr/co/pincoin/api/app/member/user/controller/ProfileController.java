package kr.co.pincoin.api.app.member.user.controller;

import jakarta.validation.Valid;
import kr.co.pincoin.api.app.member.user.request.*;
import kr.co.pincoin.api.app.member.user.response.MyUserResponse;
import kr.co.pincoin.api.app.member.user.response.UserResponse;
import kr.co.pincoin.api.app.member.user.service.ProfileService;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import kr.co.pincoin.api.global.security.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
  private final ProfileService profileService;

  @PostMapping
  public ResponseEntity<ApiResponse<UserResponse>> create(
      @Valid @RequestBody UserCreateRequest request) {
    Profile profile = profileService.createUser(request);
    return ResponseEntity.ok().body(ApiResponse.of(UserResponse.from(profile)));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<UserResponse>> getProfile(@PathVariable Integer userId) {
    Profile profile = profileService.getProfile(userId);
    return ResponseEntity.ok().body(ApiResponse.of(UserResponse.from(profile)));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<MyUserResponse>> getMyProfile(@CurrentUser User user) {
    Profile profile = profileService.getProfile(user.getId());
    return ResponseEntity.ok().body(ApiResponse.of(MyUserResponse.from(profile)));
  }

  @PatchMapping("/{userId}/username")
  public ResponseEntity<ApiResponse<UserResponse>> updateUsername(
      @PathVariable Integer userId, @Valid @RequestBody UsernameUpdateRequest request) {
    Profile profile = profileService.updateUsername(userId, request);
    return ResponseEntity.ok().body(ApiResponse.of(UserResponse.from(profile)));
  }

  @PatchMapping("/{userId}/email")
  public ResponseEntity<ApiResponse<UserResponse>> updateEmail(
      @PathVariable Integer userId, @Valid @RequestBody EmailUpdateRequest request) {
    Profile profile = profileService.updateEmail(userId, request);
    return ResponseEntity.ok().body(ApiResponse.of(UserResponse.from(profile)));
  }

  @PatchMapping("/{userId}/password")
  public ResponseEntity<ApiResponse<UserResponse>> updatePassword(
      @PathVariable Integer userId, @Valid @RequestBody PasswordUpdateRequest request) {
    Profile profile = profileService.updatePassword(userId, request);
    return ResponseEntity.ok().body(ApiResponse.of(UserResponse.from(profile)));
  }

  @PatchMapping("/{userId}/phone")
  public ResponseEntity<ApiResponse<UserResponse>> updatePhone(
      @PathVariable Integer userId, @RequestParam String newPhone) {
    Profile profile = profileService.updatePhone(userId, newPhone);
    return ResponseEntity.ok().body(ApiResponse.of(UserResponse.from(profile)));
  }

  @PatchMapping("/{userId}/profile")
  public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
      @PathVariable Integer userId, @Valid @RequestBody FullnameUpdateRequest request) {
    Profile profile =
        profileService.updateProfile(userId, request.getFirstName(), request.getLastName());
    return ResponseEntity.ok().body(ApiResponse.of(UserResponse.from(profile)));
  }

  @DeleteMapping("/{userId}/withdrawal")
  public ResponseEntity<Void> withdrawUser(
      @PathVariable Integer userId, @Valid @RequestBody UserDeleteRequest request) {
    profileService.withdrawUser(userId, request.getPassword());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(
      @PathVariable Integer userId, @Valid @RequestBody UserDeleteRequest request) {
    profileService.deleteUser(userId, request.getPassword());
    return ResponseEntity.noContent().build();
  }
}
