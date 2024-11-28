package kr.co.pincoin.api.app.admin.user.service;

import kr.co.pincoin.api.domain.auth.model.phone.enums.PhoneVerifiedStatus;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.profile.ProfileRepository;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.domain.auth.service.AbstractUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserService extends AbstractUserService {
    public AdminUserService(UserRepository userRepository,
                            ProfileRepository profileRepository,
                            PasswordEncoder passwordEncoder) {
        super(userRepository, profileRepository, passwordEncoder);
    }

    @Transactional
    public User
    createUser(String email, String password, String username, String firstName, String lastName) {
        return createUserInternal(email, password, username, firstName, lastName, false);
    }

    @Transactional
    public User
    createAdmin(String email, String password, String username, String firstName, String lastName) {
        return createUserInternal(email, password, username, firstName, lastName, true);
    }

    @Transactional
    public User
    resetPassword(Integer userId, String newPassword) {
        return resetPasswordInternal(userId, newPassword);
    }

    @Transactional
    public User
    updatePhoneVerificationStatus(Integer userId, PhoneVerifiedStatus status) {
        return updatePhoneVerificationStatusInternal(userId, status);
    }

    @Transactional
    public User
    updateDocumentVerification(Integer userId, boolean verified) {
        return updateDocumentVerificationInternal(userId, verified);
    }

    @Transactional
    public void
    forceWithdrawUser(Integer userId) {
        User user = findUser(userId);
        user.deactivate();
        userRepository.save(user);
    }

    @Transactional
    public void
    toggleUserActivation(Integer userId, boolean active) {
        User user = findUser(userId);

        if (active) {
            user.activate();
        } else {
            user.deactivate();
        }

        userRepository.save(user);
    }

    public Page<Profile>
    findAllProfiles(Pageable pageable) {
        return profileRepository.findAllWithUserFetch(pageable);
    }

    public Profile
    getProfileDetails(Integer userId) {
        return findProfile(userId);
    }
    //- 고객 문의 목록
    //- 고객 문의 상세, 답변하기
    //- 이용후기 목록
    //- 이용후기 보기
    //- 이용후기 작성
    //- 이용후기 답변 (관리)
    //- SMS 발송 내역
    //- SMS 발송
}
