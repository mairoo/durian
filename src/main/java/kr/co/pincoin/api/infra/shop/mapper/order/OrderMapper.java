package kr.co.pincoin.api.infra.shop.mapper.order;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {
  private final UserMapper userMapper;

  public Order toModel(OrderEntity entity) {
    return toModel(entity, false);
  }

  public Order toModelDetached(OrderEntity entity) {
    return toModel(entity, true);
  }

  private Order toModel(OrderEntity entity, boolean detached) {
    if (entity == null) {
      return null;
    }

    return Order.builder()
        .id(entity.getId())
        .orderNo(entity.getOrderNo())
        .fullname(entity.getFullname())
        .userAgent(entity.getUserAgent())
        .acceptLanguage(entity.getAcceptLanguage())
        .ipAddress(entity.getIpAddress())
        .paymentMethod(entity.getPaymentMethod())
        .transactionId(entity.getTransactionId())
        .status(entity.getStatus())
        .visibility(entity.getVisibility())
        .totalListPrice(entity.getTotalListPrice())
        .totalSellingPrice(entity.getTotalSellingPrice())
        .currency(entity.getCurrency())
        .message(entity.getMessage())
        .suspicious(entity.getSuspicious())
        .user(
            detached
                ? null
                : Optional.ofNullable(entity.getUser()).map(userMapper::toModel).orElse(null))
        .parent(
            detached
                ? null
                : Optional.ofNullable(entity.getParent()).map(this::toModel).orElse(null))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .removed(entity.isRemoved())
        .build();
  }

  public List<Order> toModelList(List<OrderEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public OrderEntity toEntity(Order model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<OrderEntity> toEntityList(List<Order> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
