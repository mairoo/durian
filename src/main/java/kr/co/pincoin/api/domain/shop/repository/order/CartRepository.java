package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Cart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository {

  Cart save(Cart cart);

  @Query("SELECT c FROM CartEntity c JOIN FETCH c.user WHERE c.user.id = :userId")
  Optional<Cart> findByUserId(@Param("userId") Integer userId);

  @Query("SELECT c FROM CartEntity c JOIN FETCH c.user WHERE c.user = :user")
  Optional<Cart> findByUser(@Param("user") User user);
}
