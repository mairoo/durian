package kr.co.pincoin.api.domain.auth.model.user;

import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private final Long id;

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

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "instanceBuilder")
    private User(String username,
                 String password,
                 String email,
                 String firstName,
                 String lastName,
                 boolean isSuperuser,
                 boolean isStaff) {
        this.id = null;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = true;
        this.isSuperuser = isSuperuser;
        this.isStaff = isStaff;
        this.dateJoined = LocalDateTime.now();
    }

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "jpaBuilder")
    private User(Long id,
                 String password,
                 LocalDateTime lastLogin,
                 boolean isSuperuser,
                 String username,
                 String firstName,
                 String lastName,
                 String email,
                 boolean isStaff,
                 boolean isActive,
                 LocalDateTime dateJoined) {
        this.id = id;
        this.password = password;
        this.lastLogin = lastLogin;
        this.isSuperuser = isSuperuser;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isStaff = isStaff;
        this.isActive = isActive;
        this.dateJoined = dateJoined;
    }

    public static User of(String username,
                          String password,
                          String email,
                          String firstName,
                          String lastName) {
        return User.instanceBuilder()
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
        return User.instanceBuilder()
                .username(username)
                .password(password)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .isSuperuser(true)
                .isStaff(true)
                .build();
    }

    public static User from(UserEntity entity) {
        return User.jpaBuilder()
                .id(entity.getId())
                .password(entity.getPassword())
                .lastLogin(entity.getLastLogin())
                .isSuperuser(entity.getIsSuperuser())
                .username(entity.getUsername())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .isStaff(entity.getIsStaff())
                .isActive(entity.getIsActive())
                .dateJoined(entity.getDateJoined())
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