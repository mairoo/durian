package kr.co.pincoin.api.domain.auth.repository.profile;

import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfileRepository {

  Profile save(Profile profile);

  void delete(Profile profile);

  Optional<Profile> findByUserId(Integer userId);

  Page<Profile> findAllWithUser(Pageable pageable); // findAllWithUserFetch에서 변경

  Optional<Profile> findByUserIdWithUser(Integer userId); // findByUserIdWithFetch에서 변경

  Optional<Profile> findByUserWithUser(User user); // findByUserWithFetch에서 변경
}
