package kr.co.pincoin.api.domain.auth.service;

import kr.co.pincoin.api.domain.auth.model.phone.enums.PhoneVerifiedStatus;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.profile.ProfileRepository;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    public Page<Profile>
    findProfiles(Pageable pageable) {
        return profileRepository.findAllWithUserFetch(pageable);
    }

    public Profile
    findProfile(Integer userId) {
        return profileRepository.findByUserIdWithFetch(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional
    public Profile
    createProfile(User user) {
        Profile profile = Profile.of(user);
        return profileRepository.save(profile);
    }

    @Transactional
    public User
    updatePhone(Integer userId,
                String newPhone) {
        Profile profile = findProfile(userId);
        profile.verifyPhone(newPhone);
        profileRepository.save(profile);
        return profile.getUser();
    }

    @Transactional
    public User
    updatePhoneVerification(Integer userId,
                            PhoneVerifiedStatus status) {
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
    public User
    updateDocumentVerification(Integer userId,
                               boolean verified) {
        Profile profile = findProfile(userId);
        if (verified) {
            profile.verifyDocument();
        } else {
            profile.revokeDocumentVerification();
        }
        profileRepository.save(profile);
        return profile.getUser();
    }
}