package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.Optional;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import kr.co.pincoin.api.infra.shop.entity.order.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartJpaRepository extends JpaRepository<CartEntity, Long> {

  Optional<CartEntity> findByUserId(Integer userId);

  Optional<CartEntity> findByUser(UserEntity userEntity);
}
