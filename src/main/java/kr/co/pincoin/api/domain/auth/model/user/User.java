package kr.co.pincoin.api.domain.auth.model.user;

import kr.co.pincoin.api.app.member.user.request.UserCreateRequest;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private final Integer id;

    private final LocalDateTime dateJoined;

    private String password;

    private LocalDateTime lastLogin;

    private boolean isSuperuser;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private boolean isStaff;

    private boolean isActive;

    @Builder
    private User(Integer id,
                 String username,
                 String password,
                 String email,
                 String firstName,
                 String lastName,
                 boolean isSuperuser,
                 boolean isStaff,
                 boolean isActive,
                 LocalDateTime lastLogin,
                 LocalDateTime dateJoined) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isSuperuser = isSuperuser;
        this.isStaff = isStaff;
        this.isActive = isActive;
        this.lastLogin = lastLogin;
        this.dateJoined = dateJoined != null
                ? dateJoined
                : LocalDateTime.now();
    }
    public static User of(String username,
                          String password,
                          String email,
                          String firstName,
                          String lastName) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .isSuperuser(false)
                .isStaff(false)
                .build();
    }

    public static User createAdmin(String username,
                                   String password,
                                   String email,
                                   String firstName,
                                   String lastName) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .isSuperuser(true)
                .isStaff(true)
                .build();
    }

    public static User from(UserCreateRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isSuperuser(true)
                .isStaff(true)
                .build();
    }

    public UserEntity toEntity() {
        return UserEntity.builder()
                .id(this.getId())
                .password(this.getPassword())
                .lastLogin(this.getLastLogin())
                .isSuperuser(this.isSuperuser())
                .username(this.getUsername())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .email(this.getEmail())
                .isStaff(this.isStaff())
                .isActive(this.isActive())
                .dateJoined(this.getDateJoined())
                .build();
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateLoginTime() {
        this.lastLogin = LocalDateTime.now();
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateProfile(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void grantStaffPrivileges() {
        this.isStaff = true;
    }

    public void revokeStaffPrivileges() {
        this.isStaff = false;
    }

    public void grantSuperuserPrivileges() {
        this.isSuperuser = true;
    }

    public void revokeSuperuserPrivileges() {
        this.isSuperuser = false;
    }

    public String getFullName() {
        return String.format("%s %s", lastName, firstName).trim();
    }

    public boolean isAdmin() {
        return this.isSuperuser && this.isStaff;
    }
}