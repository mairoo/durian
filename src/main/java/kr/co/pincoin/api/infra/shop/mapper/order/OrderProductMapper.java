package kr.co.pincoin.api.infra.shop.mapper.order;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProductMapper {
  private final OrderMapper orderMapper;

  public OrderProduct toModel(OrderProductEntity entity) {
    if (entity == null) {
      return null;
    }

    return OrderProduct.builder()
        .id(entity.getId())
        .name(entity.getName())
        .subtitle(entity.getSubtitle())
        .code(entity.getCode())
        .listPrice(entity.getListPrice())
        .sellingPrice(entity.getSellingPrice())
        .quantity(entity.getQuantity())
        .order(orderMapper.toModel(entity.getOrder()))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .isRemoved(entity.isRemoved())
        .build();
  }

  public OrderProductEntity toEntity(OrderProduct model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<OrderProduct> toModelList(List<OrderProductEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public List<OrderProductEntity> toEntityList(List<OrderProduct> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
