package kr.co.pincoin.api.domain.auth.model.user;

import java.time.LocalDateTime;
import kr.co.pincoin.api.app.member.user.request.UserCreateRequest;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class User {
  // 1. 불변 필드 (식별자, 생성일)
  private final Integer id;
  private final LocalDateTime dateJoined;

  // 2. 인증 정보
  private String password;
  private String email;
  private LocalDateTime lastLogin;

  // 3. 개인 정보
  private String username;
  private String firstName;
  private String lastName;

  // 4. 권한 정보
  private boolean isSuperuser;
  private boolean isStaff;
  private boolean isActive;

  @Builder
  private User( // 1. 불변 필드
      Integer id,
      LocalDateTime dateJoined,

      // 2. 인증 정보
      String password,
      String email,
      LocalDateTime lastLogin,

      // 3. 개인 정보
      String username,
      String firstName,
      String lastName,

      // 4. 권한 정보
      boolean isSuperuser,
      boolean isStaff,
      boolean isActive) {
    // 1. 불변 필드
    this.id = id;
    this.dateJoined = dateJoined != null ? dateJoined : LocalDateTime.now();

    // 2. 인증 정보
    this.password = password;
    this.email = email;
    this.lastLogin = lastLogin;

    // 3. 개인 정보
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;

    // 4. 권한 정보
    this.isSuperuser = isSuperuser;
    this.isStaff = isStaff;
    this.isActive = isActive;
  }

  public static User of(
      String email, String password, String username, String firstName, String lastName) {
    return User.builder()
        // 2. 인증 정보
        .email(email)
        .password(password)

        // 3. 개인 정보
        .username(username)
        .firstName(firstName)
        .lastName(lastName)

        // 4. 권한 정보
        .isSuperuser(false)
        .isStaff(false)
        .isActive(true)
        .build();
  }

  public static User createAdmin(
      String email, String password, String username, String firstName, String lastName) {
    return User.builder()
        // 2. 인증 정보
        .email(email)
        .password(password)

        // 3. 개인 정보
        .username(username)
        .firstName(firstName)
        .lastName(lastName)

        // 4. 권한 정보
        .isSuperuser(true)
        .isStaff(true)
        .isActive(true)
        .build();
  }

  public static User from(UserCreateRequest request) {
    return User.builder()
        // 2. 인증 정보
        .email(request.getEmail())
        .password(request.getPassword())

        // 3. 개인 정보
        .username(request.getUsername())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())

        // 4. 권한 정보
        .isSuperuser(true)
        .isStaff(true)
        .isActive(true)
        .build();
  }

  public UserEntity toEntity() {
    return UserEntity.builder()
        // 1. 불변 필드
        .id(this.getId())
        .dateJoined(this.getDateJoined())

        // 2. 인증 정보
        .password(this.getPassword())
        .email(this.getEmail())
        .lastLogin(this.getLastLogin())

        // 3. 개인 정보
        .username(this.getUsername())
        .firstName(this.getFirstName())
        .lastName(this.getLastName())

        // 4. 권한 정보
        .isSuperuser(this.isSuperuser())
        .isStaff(this.isStaff())
        .isActive(this.isActive())
        .build();
  }

  // 인증 관련
  public void validateCredentials(PasswordEncoder encoder, String rawPassword) {
    if (!encoder.matches(rawPassword, this.password)) {
      throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
    }
  }

  public void resetPassword(PasswordEncoder encoder, String newPassword) {
    this.password = encoder.encode(newPassword);
    this.updateLoginTime();
  }

  public void updatePassword(PasswordEncoder encoder, String newPassword) {
    this.password = encoder.encode(newPassword);
  }

  public void validateNewEmail(String newEmail, boolean exists) {
    if (!this.email.equals(newEmail) && exists) {
      throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
    }
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updateLoginTime() {
    this.lastLogin = LocalDateTime.now();
  }

  // 개인정보 관리
  public void validateNewUsername(String newUsername, boolean exists) {
    if (!this.username.equals(newUsername) && exists) {
      throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
    }
  }

  public void updateUsername(String username) {
    this.username = username;
  }

  public void updateProfile(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getFullName() {
    return String.format("%s %s", lastName, firstName).trim();
  }

  // 계정 상태 관련
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

  public boolean isAdmin() {
    return this.isSuperuser && this.isStaff;
  }

  public boolean isWithdrawn() {
    return !this.isActive;
  }

  public boolean canLogin() {
    return this.isActive && !this.isSuperuser;
  }

  public boolean canLoginAsAdmin() {
    return this.isActive && this.isSuperuser && this.isStaff;
  }
}
