package kr.co.pincoin.api.domain.auth.service;

import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSecurityService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserValidationService userValidationService;

    private final ProfileDomainService profileDomainService;

    @Transactional
    public Profile
    createUser(String email,
               String password,
               String username,
               String firstName,
               String lastName,
               boolean isAdmin) {
        userValidationService.validateNewUser(email, username);

        User user = isAdmin ?
                User.createAdmin(email, passwordEncoder.encode(password), username, firstName, lastName) :
                User.of(email, passwordEncoder.encode(password), username, firstName, lastName);

        User savedUser = userRepository.save(user);

        return profileDomainService.createProfile(savedUser);
    }

    @Transactional
    public Profile
    updateEmail(Integer userId,
                String newEmail) {
        User user = userValidationService.findUser(userId);

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        user.updateEmail(newEmail);
        userRepository.save(user);

        return profileDomainService.createProfile(user);
    }

    @Transactional
    public Profile
    updatePassword(Integer userId,
                   String currentPassword,
                   String newPassword) {
        User user = userValidationService.findUser(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return profileDomainService.createProfile(user);
    }

    @Transactional
    public Profile
    resetPassword(Integer userId,
                  String newPassword) {
        User user = userValidationService.findUser(userId);
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return profileDomainService.createProfile(user);
    }
}
