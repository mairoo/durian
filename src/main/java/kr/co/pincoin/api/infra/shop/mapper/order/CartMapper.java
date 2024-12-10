package kr.co.pincoin.api.infra.shop.mapper.order;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.Cart;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import kr.co.pincoin.api.infra.shop.entity.order.CartEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final UserMapper userMapper;

    public Cart toModel(CartEntity entity) {
        if (entity == null) {
            return null;
        }

        return Cart.builder()
            .id(entity.getId())
            .user(userMapper.toModel(entity.getUser()))
            .cartData(entity.getCartData())
            .version(entity.getVersion())
            .build();
    }

    public CartEntity toEntity(Cart model) {
        if (model == null) {
            return null;
        }

        return model.toEntity();
    }

    public List<Cart> toModelList(List<CartEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
            .map(this::toModel)
            .collect(Collectors.toList());
    }

    public List<CartEntity> toEntityList(List<Cart> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }
}