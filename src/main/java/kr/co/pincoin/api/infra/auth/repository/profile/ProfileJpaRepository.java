package kr.co.pincoin.api.infra.auth.repository.profile;

import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileJpaRepository extends JpaRepository<Profile, Long> {
}