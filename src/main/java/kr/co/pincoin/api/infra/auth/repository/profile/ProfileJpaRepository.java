package kr.co.pincoin.api.infra.auth.repository.profile;

import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.infra.auth.entity.profile.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Long> {
    @Query("SELECT p FROM ProfileEntity p JOIN FETCH p.user WHERE p.user = :user")
    Optional<ProfileEntity> findByUserWithFetch(@Param("user") User user);
}