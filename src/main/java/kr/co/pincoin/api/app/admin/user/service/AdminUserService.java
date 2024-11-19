package kr.co.pincoin.api.app.admin.user.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserService {
    public void
    createUser() {

    }

    public void
    findAllUsers() {

    }

    public void
    findUserById() {

    }

    public void
    updateUserName() {

    }

    public void
    updateUserEmail() {

    }

    public void
    updateUserPassword() {

    }

    public void
    deleteUser() {

    }
}
