package kr.co.pincoin.api.app.admin.user.controller;

import kr.co.pincoin.api.app.admin.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public void createUser() {

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