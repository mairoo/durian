package kr.co.pincoin.api.app.member.user.controller;

import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>>
    create(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(user)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>>
    find(@PathVariable Integer userId) {
        User user = userService.find(userId);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(user)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyUserResponse>>
    findMe(@CurrentUser User user) {
        return ResponseEntity.ok()
                .body(ApiResponse.of(MyUserResponse.from(user)));
    }

    @PatchMapping("/{userId}/username")
    public ResponseEntity<ApiResponse<UserResponse>>
    updateUsername(@PathVariable Integer userId,
                   @Valid @RequestBody UsernameUpdateRequest request) {
        User updatedUser = userService.updateUsername(userId, request);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(updatedUser)));
    }

    @PatchMapping("/{userId}/email")
    public ResponseEntity<ApiResponse<UserResponse>>
    updateEmail(@PathVariable Integer userId,
                @Valid @RequestBody EmailUpdateRequest request) {
        User updatedUser = userService.updateEmail(userId, request);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(updatedUser)));
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<UserResponse>>
    updatePassword(@PathVariable Integer userId,
                   @Valid @RequestBody PasswordUpdateRequest request) {
        User updatedUser = userService.updatePassword(userId, request);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(updatedUser)));
    }

    @PatchMapping("/{id}/phone")
    public ResponseEntity<ApiResponse<UserResponse>>
    updatePhone(@PathVariable Integer id,
                @RequestParam String newPhone) {
        User updatedUser = userService.updatePhone(id, newPhone);
        return ResponseEntity.ok()
                .body(ApiResponse.of(UserResponse.from(updatedUser)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    withdrawUser(@PathVariable Integer id,
                 @RequestParam String currentPassword) {
        userService.withdrawUser(id, currentPassword);
        return ResponseEntity.noContent().build();
    }
}