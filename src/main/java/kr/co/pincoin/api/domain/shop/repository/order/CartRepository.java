package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Cart;

public interface CartRepository {

    Cart save(Cart cart);

    Optional<Cart> findByUserId(Integer userId);

    Optional<Cart> findByUser(User user);
}
