package kr.co.pincoin.api.infra.auth.service;

import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.profile.ProfileRepository;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserProfilePersistenceService {
  private final UserRepository userRepository;

  private final ProfileRepository profileRepository;

  public User findUser(Integer userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
  }

  public Profile findProfile(Integer userId) {
    return profileRepository
        .findByUserIdWithUser(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
  }

  public Page<Profile> findProfiles(Pageable pageable) {
    return profileRepository.findAllWithUser(pageable);
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  @Transactional
  public Profile createUserAndProfile(User user) {
    User savedUser = userRepository.save(user);
    Profile profile = Profile.of(savedUser);
    return profileRepository.save(profile);
  }

  @Transactional
  public Profile updateUser(User user) {
    userRepository.save(user);
    return findProfile(user.getId());
  }

  @Transactional
  public Profile updateProfile(Profile profile) {
    return profileRepository.save(profile);
  }

  @Transactional
  public void deleteUserAndProfile(Integer userId) {
    Profile profile = findProfile(userId);
    User user = profile.getUser();

    profileRepository.delete(profile);
    userRepository.delete(user);
  }

  @Transactional
  public Profile updateUserAndProfile(User user, Profile profile) {
    userRepository.save(user);
    return profileRepository.save(profile);
  }
}
