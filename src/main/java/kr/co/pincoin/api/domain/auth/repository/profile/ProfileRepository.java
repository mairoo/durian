package kr.co.pincoin.api.domain.auth.repository.profile;

import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;

import java.util.Optional;

public interface ProfileRepository {
    Profile save(Profile profile);

    Optional<Profile> findByUserIdWithFetch(Integer userId);

    Optional<Profile> findByUserWithFetch(User user);

    void delete(Profile profile);
}
