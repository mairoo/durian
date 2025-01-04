package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Cart;
import org.springframework.data.repository.query.Param;

public interface CartRepository {
  Cart save(Cart cart);

  Optional<Cart> findByUser(@Param("user") User user);
}
