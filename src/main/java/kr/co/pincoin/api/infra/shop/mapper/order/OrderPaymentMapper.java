package kr.co.pincoin.api.infra.shop.mapper.order;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.infra.shop.entity.order.OrderPaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPaymentMapper {
  private final OrderMapper orderMapper;

  public OrderPayment toModel(OrderPaymentEntity entity) {
    if (entity == null) {
      return null;
    }

    return OrderPayment.builder()
        .id(entity.getId())
        .account(entity.getAccount())
        .amount(entity.getAmount())
        .balance(entity.getBalance())
        .received(entity.getReceived())
        .order(Optional.ofNullable(entity.getOrder()).map(orderMapper::toModel).orElse(null))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .isRemoved(entity.isRemoved())
        .build();
  }

  public List<OrderPayment> toModelList(List<OrderPaymentEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public OrderPaymentEntity toEntity(OrderPayment model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<OrderPaymentEntity> toEntityList(List<OrderPayment> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
