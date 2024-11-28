package kr.co.pincoin.api.domain.auth.service;

import kr.co.pincoin.api.domain.auth.model.phone.enums.PhoneVerifiedStatus;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.profile.ProfileRepository;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public abstract class AbstractUserService {
    protected final UserRepository userRepository;

    protected final ProfileRepository profileRepository;

    protected final PasswordEncoder passwordEncoder;

    @Transactional
    protected User
    createUserInternal(String email,
                       String password,
                       String username,
                       String firstName,
                       String lastName,
                       boolean isAdmin) {
        validateNewUser(email, username);

        User user = isAdmin ?
                User.createAdmin(email, passwordEncoder.encode(password), username, firstName, lastName) :
                User.of(email, passwordEncoder.encode(password), username, firstName, lastName);

        User savedUser = userRepository.save(user);
        Profile profile = Profile.of(savedUser);
        profileRepository.save(profile);

        return savedUser;
    }

    @Transactional
    protected User
    updateEmailInternal(Integer userId, String newEmail) {
        User user = findUser(userId);

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        user.updateEmail(newEmail);
        return userRepository.save(user);
    }

    @Transactional
    protected User
    updatePhoneInternal(Integer userId, String newPhone) {
        Profile profile = findProfile(userId);

        profile.verifyPhone(newPhone);
        profileRepository.save(profile);

        return profile.getUser();
    }

    @Transactional
    protected User
    updatePasswordInternal(Integer userId, String currentPassword, String newPassword) {
        User user = findUser(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    @Transactional
    protected User
    resetPasswordInternal(Integer userId, String newPassword) {
        User user = findUser(userId);
        user.updatePassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    @Transactional
    protected User
    updatePhoneVerificationStatusInternal(Integer userId, PhoneVerifiedStatus status) {
        Profile profile = findProfile(userId);

        switch (status) {
            case VERIFIED:
                profile.verifyPhone(profile.getPhone());
                break;
            case UNVERIFIED:
            case REVOKED:
                profile.revokePhoneVerification();
                break;
            default:
                throw new BusinessException(ErrorCode.INVALID_PHONE_VERIFICATION_STATUS);
        }

        profileRepository.save(profile);

        return profile.getUser();
    }

    @Transactional
    protected User
    updateDocumentVerificationInternal(Integer userId, boolean verified) {
        Profile profile = findProfile(userId);

        if (verified) {
            profile.verifyDocument();
        } else {
            profile.revokeDocumentVerification();
        }

        profileRepository.save(profile);

        return profile.getUser();
    }

    protected void
    validateNewUser(String email, String username) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    protected User
    findUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    protected Profile
    findProfile(Integer userId) {
        return profileRepository.findByUserIdWithFetch(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
