package kr.co.pincoin.api.infra.auth.repository.profile;

import kr.co.pincoin.api.infra.auth.entity.profile.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Long> {
}