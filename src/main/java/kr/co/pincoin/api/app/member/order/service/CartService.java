package kr.co.pincoin.api.app.member.order.service;

import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Cart;
import kr.co.pincoin.api.domain.shop.repository.order.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public Cart getCart(User user) {
        return cartRepository.findByUser(user)
            .orElseGet(() -> Cart.createEmptyCart(user));
    }

    @Transactional
    public Cart syncCart(User user, Cart cart) {
        Cart existingCart = cartRepository.findByUser(user)
            .orElseGet(() -> Cart.createEmptyCart(user));

        return cartRepository.save(existingCart.updateCartData(cart.getCartData()));
    }
}
