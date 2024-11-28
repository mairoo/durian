package kr.co.pincoin.api.domain.auth.service;

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

    private final ProfileService profileService;

    @Transactional
    public User
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
        profileService.createProfile(savedUser);

        return savedUser;
    }

    @Transactional
    public User
    updateEmail(Integer userId,
                String newEmail) {
        User user = userValidationService.findUser(userId);

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        user.updateEmail(newEmail);
        return userRepository.save(user);
    }

    @Transactional
    public User
    updatePassword(Integer userId,
                   String currentPassword,
                   String newPassword) {
        User user = userValidationService.findUser(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    @Transactional
    public User
    resetPassword(Integer userId,
                  String newPassword) {
        User user = userValidationService.findUser(userId);
        user.updatePassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
