package kr.co.pincoin.api.app.member.user.service;

import kr.co.pincoin.api.app.member.user.request.EmailUpdateRequest;
import kr.co.pincoin.api.app.member.user.request.PasswordUpdateRequest;
import kr.co.pincoin.api.app.member.user.request.UserCreateRequest;
import kr.co.pincoin.api.app.member.user.request.UsernameUpdateRequest;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.domain.auth.service.ProfileService;
import kr.co.pincoin.api.domain.auth.service.UserSecurityService;
import kr.co.pincoin.api.domain.auth.service.UserValidationService;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserSecurityService userSecurityService;

    private final ProfileService profileService;

    private final UserValidationService userValidationService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Profile
    createUser(UserCreateRequest request) {
        return userSecurityService.createUser(request.getEmail(),
                                              request.getPassword(),
                                              request.getUsername(),
                                              request.getFirstName(),
                                              request.getLastName(),
                                              false);
    }

    public Profile
    find(Integer userId) {
        return userValidationService.findProfile(userId);
    }

    @Transactional
    public Profile
    updateUsername(Integer userId, UsernameUpdateRequest request) {
        Profile profile = userValidationService.findProfile(userId);

        User user = profile.getUser();
        user.updateUsername(request.getUsername());
        userRepository.save(user);

        return profile;
    }

    @Transactional
    public Profile
    updateEmail(Integer userId, EmailUpdateRequest request) {
        return userSecurityService.updateEmail(userId, request.getEmail());
    }

    @Transactional
    public Profile
    updatePassword(Integer userId, PasswordUpdateRequest request) {
        return userSecurityService.updatePassword(userId,
                                                  request.getCurrentPassword(),
                                                  request.getNewPassword());
    }

    @Transactional
    public Profile
    updatePhone(Integer userId, String newPhone) {
        return profileService.updatePhone(userId, newPhone);
    }

    @Transactional
    public void
    withdrawUser(Integer userId, String currentPassword) {
        User user = userValidationService.findUser(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        user.deactivate();
        userRepository.save(user);
    }
}