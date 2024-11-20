package kr.co.pincoin.api.app.member.user.controller;

import jakarta.validation.Valid;
import kr.co.pincoin.api.app.admin.user.service.AdminUserService;
import kr.co.pincoin.api.app.member.user.request.EmailUpdateRequest;
import kr.co.pincoin.api.app.member.user.request.PasswordUpdateRequest;
import kr.co.pincoin.api.app.member.user.request.UserCreateRequest;
import kr.co.pincoin.api.app.member.user.request.UsernameUpdateRequest;
import kr.co.pincoin.api.app.member.user.response.MyUserResponse;
import kr.co.pincoin.api.app.member.user.response.UserResponse;
import kr.co.pincoin.api.app.member.user.service.UserService;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import kr.co.pincoin.api.global.security.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final AdminUserService adminUserService;
    private final UserService userService;

    // Create
    @PostMapping("")
    public ResponseEntity<ApiResponse<UserResponse>>
    create(@Valid @RequestBody UserCreateRequest request) {
        User user = User.of("username", "password", "email", "firstName", "lastName");
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(user)));
    }

    // Read
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<UserResponse>>>
    findAll() {
        // Implementation
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>>
    find(@PathVariable Long id) {
        User user = userService.find(id);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(user)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyUserResponse>>
    findMe(@CurrentUser User user) {
        return ResponseEntity.ok()
                .body(ApiResponse.of(MyUserResponse.from(user)));
    }

    // Update
    @PatchMapping("/{id}/username")
    public ResponseEntity<ApiResponse<UserResponse>>
    updateUsername(@PathVariable Long id,
                   @Valid @RequestBody UsernameUpdateRequest request) {
        User updatedUser = userService.updateUsername(id, request);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(updatedUser)));
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<ApiResponse<UserResponse>>
    updateEmail(@PathVariable Long id,
                @Valid @RequestBody EmailUpdateRequest request) {
        User updatedUser = userService.updateEmail(id, request);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(updatedUser)));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<UserResponse>>
    updatePassword(@PathVariable Long id,
                   @Valid @RequestBody PasswordUpdateRequest request) {
        User updatedUser = userService.updatePassword(id, request);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(updatedUser)));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    delete(@PathVariable Long id) {
        // Implementation
        return ResponseEntity.noContent().build();
    }
}