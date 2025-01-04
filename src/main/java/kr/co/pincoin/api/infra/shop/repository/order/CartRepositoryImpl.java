package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Cart;
import kr.co.pincoin.api.domain.shop.repository.order.CartRepository;
import kr.co.pincoin.api.infra.shop.mapper.order.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {
  private final CartJpaRepository jpaRepository;

  private final CartMapper mapper;

  @Override
  public Cart save(Cart cart) {
    return mapper.toModel(jpaRepository.save(mapper.toEntity(cart)));
  }

  @Override
  public Optional<Cart> findByUser(User user) {
    return jpaRepository.findByUser(user.toEntity()).map(mapper::toModel);
  }
}
