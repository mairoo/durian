package kr.co.pincoin.api.app.admin.user.service;

import kr.co.pincoin.api.domain.auth.model.phone.enums.PhoneVerifiedStatus;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.domain.auth.service.ProfileService;
import kr.co.pincoin.api.domain.auth.service.UserSecurityService;
import kr.co.pincoin.api.domain.auth.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserService {
    private final UserSecurityService userSecurityService;

    private final ProfileService profileService;

    private final UserValidationService userValidationService;

    private final UserRepository userRepository;

    @Transactional
    public User
    createUser(String email,
               String password,
               String username,
               String firstName,
               String lastName) {
        return userSecurityService.createUser(email, password, username, firstName, lastName, false);
    }

    @Transactional
    public User
    createAdmin(String email,
                String password,
                String username,
                String firstName,
                String lastName) {
        return userSecurityService.createUser(email, password, username, firstName, lastName, true);
    }

    @Transactional
    public User
    resetPassword(Integer userId,
                  String newPassword) {
        return userSecurityService.resetPassword(userId, newPassword);
    }

    @Transactional
    public User
    updatePhoneVerification(Integer userId,
                            PhoneVerifiedStatus status) {
        return profileService.updatePhoneVerification(userId, status);
    }

    @Transactional
    public User
    updateDocumentVerification(Integer userId,
                               boolean verified) {
        return profileService.updateDocumentVerification(userId, verified);
    }

    @Transactional
    public void
    forceWithdrawUser(Integer userId) {
        User user = userValidationService.findUser(userId);
        user.deactivate();
        userRepository.save(user);
    }

    @Transactional
    public void
    toggleUserActivation(Integer userId,
                         boolean active) {
        User user = userValidationService.findUser(userId);
        if (active) {
            user.activate();
        } else {
            user.deactivate();
        }
        userRepository.save(user);
    }

    public Page<Profile>
    findProfiles(Pageable pageable) {
        return profileService.findProfiles(pageable);
    }

    public Profile
    findProfile(Integer userId) {
        return profileService.findProfile(userId);
    }
}