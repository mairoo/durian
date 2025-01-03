package kr.co.pincoin.api.infra.shop.mapper.order;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductProjection;
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
        .order(Optional.ofNullable(entity.getOrder()).map(orderMapper::toModel).orElse(null))
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

  public OrderProduct mapToDomain(OrderProductProjection projection) {
    if (projection == null) {
      return null;
    }

    return OrderProduct.builder()
        .id(projection.orderProduct().getId())
        .name(projection.orderProduct().getName())
        .subtitle(projection.orderProduct().getSubtitle())
        .code(projection.orderProduct().getCode())
        .listPrice(projection.orderProduct().getListPrice())
        .sellingPrice(projection.orderProduct().getSellingPrice())
        .quantity(projection.orderProduct().getQuantity())
        .order(Optional.ofNullable(projection.order()).map(orderMapper::toModel).orElse(null))
        .created(projection.orderProduct().getCreated())
        .modified(projection.orderProduct().getModified())
        .isRemoved(projection.orderProduct().isRemoved())
        .build();
  }

  public List<OrderProduct> mapToDomainList(List<OrderProductProjection> projections) {
    if (projections == null) {
      return Collections.emptyList();
    }

    return projections.stream().map(this::mapToDomain).collect(Collectors.toList());
  }
}
