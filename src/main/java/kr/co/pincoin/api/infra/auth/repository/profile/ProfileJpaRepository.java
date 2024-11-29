package kr.co.pincoin.api.infra.auth.repository.profile;

import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.infra.auth.entity.profile.ProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Long> {
  @Query("SELECT p FROM ProfileEntity p " + "LEFT JOIN FETCH p.user u " + "ORDER BY p.created DESC")
  Page<ProfileEntity> findAllWithUser(Pageable pageable);

  Optional<ProfileEntity> findByUserId(Integer userId);

  @Query("SELECT p FROM ProfileEntity p JOIN FETCH p.user u WHERE u.id = :userId")
  Optional<ProfileEntity> findByUserIdWithUser(@Param("userId") Integer userId);

  @Query("SELECT p FROM ProfileEntity p JOIN FETCH p.user WHERE p.user = :user")
  Optional<ProfileEntity> findByUserWithUser(@Param("user") User user);
}
