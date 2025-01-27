package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.Optional;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import kr.co.pincoin.api.infra.shop.entity.order.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartJpaRepository extends JpaRepository<CartEntity, Long> {
  @Query("SELECT c FROM CartEntity c JOIN FETCH c.user WHERE c.user = :user")
  Optional<CartEntity> findByUser(@Param("user") UserEntity userEntity);
}
