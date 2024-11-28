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
    public ResponseEntity<ApiResponse<UserResponse>>
    create(@Valid @RequestBody UserCreateRequest request) {
        Profile profile = profileService.createUser(request);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(profile)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>>
    find(@PathVariable Integer userId) {
        Profile profile = profileService.find(userId);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(profile)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyUserResponse>>
    findMe(@CurrentUser User user) {
        Profile profile = profileService.find(user.getId());
        return ResponseEntity.ok()
                .body(ApiResponse.of(MyUserResponse.from(profile)));
    }

    @PatchMapping("/{userId}/username")
    public ResponseEntity<ApiResponse<UserResponse>>
    updateUsername(@PathVariable Integer userId,
                   @Valid @RequestBody UsernameUpdateRequest request) {
        Profile updatedProfile = profileService.updateUsername(userId, request);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(updatedProfile)));
    }

    @PatchMapping("/{userId}/email")
    public ResponseEntity<ApiResponse<UserResponse>>
    updateEmail(@PathVariable Integer userId,
                @Valid @RequestBody EmailUpdateRequest request) {
        Profile updatedProfile = profileService.updateEmail(userId, request);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(updatedProfile)));
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<UserResponse>>
    updatePassword(@PathVariable Integer userId,
                   @Valid @RequestBody PasswordUpdateRequest request) {
        Profile updatedProfile = profileService.updatePassword(userId, request);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(updatedProfile)));
    }

    @PatchMapping("/{id}/phone")
    public ResponseEntity<ApiResponse<UserResponse>>
    updatePhone(@PathVariable Integer id,
                @RequestParam String newPhone) {
        Profile updatedProfile = profileService.updatePhone(id, newPhone);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(updatedProfile)));
    }

    @DeleteMapping("/{id}/withdrawal")
    public ResponseEntity<Void>
    withdrawUser(@PathVariable Integer id,
                 @Valid @RequestBody UserDeleteRequest request) {
        profileService.withdrawUser(id, request.getPassword());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteUser(@PathVariable Integer id,
               @Valid @RequestBody UserDeleteRequest request) {
        profileService.deleteUser(id, request.getPassword());
        return ResponseEntity.noContent().build();
    }
}