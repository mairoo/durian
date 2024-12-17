package kr.co.pincoin.api.app.admin.user.service;

import java.math.BigDecimal;
import kr.co.pincoin.api.domain.auth.model.phone.enums.PhoneVerifiedStatus;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.global.security.annotation.SuperUser;
import kr.co.pincoin.api.infra.auth.service.UserProfilePersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@SuperUser
@RequiredArgsConstructor
public class AdminProfileService {
  private final UserProfilePersistenceService persistenceService;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public Profile createUser(
      String email, String password, String username, String firstName, String lastName) {
    // 중복 검증
    persistenceService.existsByEmail(email);
    persistenceService.existsByUsername(username);

    // 일반 사용자 생성
    User user = User.of(email, passwordEncoder.encode(password), username, firstName, lastName);

    return persistenceService.createUserAndProfile(user);
  }

  @Transactional
  public Profile createAdmin(
      String email, String password, String username, String firstName, String lastName) {
    // 중복 검증
    persistenceService.existsByEmail(email);
    persistenceService.existsByUsername(username);

    // 관리자 사용자 생성
    User user =
        User.createAdmin(email, passwordEncoder.encode(password), username, firstName, lastName);

    return persistenceService.createUserAndProfile(user);
  }

  @Transactional
  public Profile resetPassword(Integer userId, String newPassword) {
    Profile profile = persistenceService.findProfile(userId);
    User user = profile.getUser();

    user.resetPassword(passwordEncoder, newPassword);
    return persistenceService.updateUser(user);
  }

  @Transactional
  public Profile updatePhoneVerification(Integer userId, PhoneVerifiedStatus status) {
    Profile profile = persistenceService.findProfile(userId);
    profile.verifyPhoneWithStatus(status);
    return persistenceService.updateProfile(profile);
  }

  @Transactional
  public Profile updateDocumentVerification(Integer userId, boolean verified) {
    Profile profile = persistenceService.findProfile(userId);

    if (verified) {
      profile.verifyDocument();
    } else {
      profile.revokeDocumentVerification();
    }

    return persistenceService.updateProfile(profile);
  }

  @Transactional
  public Profile toggleOrderPermission(Integer userId, boolean allowOrder) {
    Profile profile = persistenceService.findProfile(userId);

    if (allowOrder) {
      profile.allowOrder();
    } else {
      profile.disallowOrder();
    }

    return persistenceService.updateProfile(profile);
  }

  @Transactional
  public void forceWithdrawUser(Integer userId) {
    Profile profile = persistenceService.findProfile(userId);
    User user = profile.getUser();

    user.deactivate();
    persistenceService.updateUser(user);
  }

  @Transactional
  public void toggleUserActivation(Integer userId, boolean active) {
    Profile profile = persistenceService.findProfile(userId);
    User user = profile.getUser();

    if (active) {
      user.activate();
    } else {
      user.deactivate();
    }

    persistenceService.updateUser(user);
  }

  @Transactional
  public Profile updateMileage(Integer userId, boolean isAdd, int amount) {
    Profile profile = persistenceService.findProfile(userId);

    if (isAdd) {
      profile.addMileage(new BigDecimal(amount));
    } else {
      profile.subtractMileage(new BigDecimal(amount));
    }

    return persistenceService.updateProfile(profile);
  }

  @Transactional
  public Profile updateMemo(Integer userId, String memo) {
    Profile profile = persistenceService.findProfile(userId);
    profile.updateMemo(memo);
    return persistenceService.updateProfile(profile);
  }

  public Page<Profile> findProfiles(Pageable pageable) {
    return persistenceService.findProfiles(pageable);
  }

  public Profile findProfile(Integer userId) {
    return persistenceService.findProfile(userId);
  }

  // 관리자 관련 추가 기능
  @Transactional
  public Profile grantAdminPrivileges(Integer userId) {
    Profile profile = persistenceService.findProfile(userId);
    User user = profile.getUser();

    user.grantSuperuserPrivileges();
    user.grantStaffPrivileges();

    return persistenceService.updateUser(user);
  }

  @Transactional
  public Profile revokeAdminPrivileges(Integer userId) {
    Profile profile = persistenceService.findProfile(userId);
    User user = profile.getUser();

    user.revokeSuperuserPrivileges();
    user.revokeStaffPrivileges();

    return persistenceService.updateUser(user);
  }
}
