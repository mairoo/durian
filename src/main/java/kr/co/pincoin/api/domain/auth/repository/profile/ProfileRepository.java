package kr.co.pincoin.api.domain.auth.repository.profile;

import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfileRepository {
  Profile save(Profile profile);

  Page<Profile> findAllWithUserFetch(Pageable pageable);

  Optional<Profile> findByUserIdWithFetch(Integer userId);

  Optional<Profile> findByUserWithFetch(User user);

  void delete(Profile profile);
}
