package kr.co.pincoin.api.app.member.user.service;

import kr.co.pincoin.api.app.member.user.request.EmailUpdateRequest;
import kr.co.pincoin.api.app.member.user.request.PasswordUpdateRequest;
import kr.co.pincoin.api.app.member.user.request.UserCreateRequest;
import kr.co.pincoin.api.app.member.user.request.UsernameUpdateRequest;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.profile.ProfileRepository;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.domain.auth.service.AbstractUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService extends AbstractUserService {
    public UserService(UserRepository userRepository,
                       ProfileRepository profileRepository,
                       PasswordEncoder passwordEncoder) {
        super(userRepository, profileRepository, passwordEncoder);
    }

    @Transactional
    public User
    createUser(UserCreateRequest request) {
        return createUserInternal(request.getEmail(),
                                  request.getPassword(),
                                  request.getUsername(),
                                  request.getFirstName(),
                                  request.getLastName(),
                                  false);
    }

    public User
    find(Integer userId) {
        return findUser(userId);
    }

    @Transactional
    public User
    updateUsername(Integer userId, UsernameUpdateRequest request) {
        User user = findUser(userId);
        user.updateUsername(request.getUsername());
        return userRepository.save(user);
    }

    @Transactional
    public User
    updateEmail(Integer userId, EmailUpdateRequest request) {
        return updateEmailInternal(userId, request.getEmail());
    }

    @Transactional
    public User
    updatePassword(Integer userId, PasswordUpdateRequest request) {
        return updatePasswordInternal(userId, request.getCurrentPassword(), request.getNewPassword());
    }

    @Transactional
    public User
    updatePhone(Integer userId, String newPhone) {
        return updatePhoneInternal(userId, newPhone);
    }
}