package kr.co.pincoin.api.app.member.user.service;

import kr.co.pincoin.api.app.member.user.request.EmailUpdateRequest;
import kr.co.pincoin.api.app.member.user.request.PasswordUpdateRequest;
import kr.co.pincoin.api.app.member.user.request.UserCreateRequest;
import kr.co.pincoin.api.app.member.user.request.UsernameUpdateRequest;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.infra.auth.service.UserProfilePersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {
    private final UserProfilePersistenceService persistenceService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Profile createUser(UserCreateRequest request) {
        // 중복 검증
        persistenceService.existsByEmail(request.getEmail());
        persistenceService.existsByUsername(request.getUsername());

        // 일반 사용자 생성
        User user = User.of(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getUsername(),
                request.getFirstName(),
                request.getLastName()
                           );

        return persistenceService.createUserAndProfile(user);
    }

    public Profile getProfile(Integer userId) {
        return persistenceService.findProfile(userId);
    }

    @Transactional
    public Profile updateUsername(Integer userId, UsernameUpdateRequest request) {
        Profile profile = persistenceService.findProfile(userId);
        User user = profile.getUser();

        // 사용자명 중복 검증
        user.validateNewUsername(request.getUsername(),
                                 persistenceService.existsByUsername(request.getUsername()));

        user.updateUsername(request.getUsername());
        return persistenceService.updateUser(user);
    }

    @Transactional
    public Profile updateEmail(Integer userId, EmailUpdateRequest request) {
        Profile profile = persistenceService.findProfile(userId);
        User user = profile.getUser();

        // 이메일 중복 검증
        user.validateNewEmail(request.getEmail(),
                              persistenceService.existsByEmail(request.getEmail()));

        user.updateEmail(request.getEmail());
        return persistenceService.updateUser(user);
    }

    @Transactional
    public Profile updatePassword(Integer userId, PasswordUpdateRequest request) {
        Profile profile = persistenceService.findProfile(userId);
        User user = profile.getUser();

        // 현재 비밀번호 검증
        user.validateCredentials(passwordEncoder, request.getCurrentPassword());

        user.updatePassword(passwordEncoder, request.getNewPassword());
        return persistenceService.updateUser(user);
    }

    @Transactional
    public Profile updatePhone(Integer userId, String newPhone) {
        Profile profile = persistenceService.findProfile(userId);
        profile.verifyPhone(newPhone);
        return persistenceService.updateProfile(profile);
    }

    @Transactional
    public void withdrawUser(Integer userId, String password) {
        Profile profile = persistenceService.findProfile(userId);
        User user = profile.getUser();

        // 비밀번호 검증
        user.validateCredentials(passwordEncoder, password);

        user.deactivate();
        persistenceService.updateUser(user);
    }

    @Transactional
    public void deleteUser(Integer userId, String password) {
        Profile profile = persistenceService.findProfile(userId);
        User user = profile.getUser();

        // 비밀번호 검증
        user.validateCredentials(passwordEncoder, password);

        persistenceService.deleteUserAndProfile(userId);
    }

    @Transactional
    public Profile updateProfile(Integer userId,
                                 String firstName,
                                 String lastName) {
        Profile profile = persistenceService.findProfile(userId);
        User user = profile.getUser();

        user.updateProfile(firstName, lastName);
        return persistenceService.updateUser(user);
    }

    @Transactional
    public Profile updateAddress(Integer userId, String address) {
        Profile profile = persistenceService.findProfile(userId);
        profile.updateAddress(address);
        return persistenceService.updateProfile(profile);
    }

    @Transactional
    public Profile updateCard(Integer userId, String card) {
        Profile profile = persistenceService.findProfile(userId);
        profile.updateCard(card);
        return persistenceService.updateProfile(profile);
    }
}