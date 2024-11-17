package kr.co.pincoin.api.infra.shop.mapper.order;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.domain.auth.model.user.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public Order toModel(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        return Order.from(entity);
    }

    public OrderEntity toEntity(Order model) {
        if (model == null) {
            return null;
        }

        return model.toEntity();
    }

    public List<Order> toModelList(List<OrderEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<OrderEntity> toEntityList(List<Order> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}