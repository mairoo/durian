package kr.co.pincoin.api.app.member.user.controller;

import jakarta.validation.Valid;
import kr.co.pincoin.api.app.admin.user.service.AdminUserService;
import kr.co.pincoin.api.app.member.user.request.UserCreateRequest;
import kr.co.pincoin.api.app.member.user.response.UserResponse;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class MemberUserController {
    private final AdminUserService adminUserService;

    private final UserMapper userMapper;  // 또는 Response 클래스 내 정적 팩토리 메서드 사용

    @PostMapping("")
    public ResponseEntity<UserResponse>
    createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = User.of("username", "password", "email", "firstName", "lastName");
        log.error("{} {} {}", user.getUsername(), user.getPassword(), user.getEmail());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @GetMapping
    public void findAllUsers() {

    }

    @GetMapping("/{id}")
    public void findUserById() {

    }

    @PatchMapping("/{id}/name")
    public void updateUserName() {

    }

    @PatchMapping("/{id}/email")
    public void updateUserEmail() {

    }

    @PatchMapping("/{id}/password")
    public void updateUserPassword() {

    }

    @DeleteMapping("/{id}")
    public void deleteUser() {

    }
}