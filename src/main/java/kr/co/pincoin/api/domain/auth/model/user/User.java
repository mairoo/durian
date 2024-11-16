package kr.co.pincoin.api.domain.auth.model.user;


import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private final Long id; // 변경 불가능한 식별자

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

    // 1. private 생성자: 내부 빌더 호출, 외부 호출 불가
    @Builder(access = AccessLevel.PRIVATE)
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
        this.dateJoined = dateJoined != null ? dateJoined : LocalDateTime.now();
    }

    // 2. 외부 사용 팩토리 메소드
    // 2-1. 기본 사용자 생성 - of
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
                .isActive(true)
                .isSuperuser(false)
                .isStaff(false)
                .dateJoined(LocalDateTime.now())
                .build(); // 이 때 private 생성자 호출
    }

    // 2-2. 엔티티로부터 도메인 모델 생성을 위한 팩토리 메서드 추가
    public static User from(UserEntity entity) {
        return User.builder()
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

    // 2-3. 관리자 생성 특별한 로직 - create
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
                .isActive(true)
                .isSuperuser(true)
                .isStaff(true)
                .dateJoined(LocalDateTime.now())
                .build();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateLoginTime() {
        this.lastLogin = LocalDateTime.now();
    }

    public void updateProfile(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
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

