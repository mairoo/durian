package kr.co.pincoin.api.infra.shop.mapper.order;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductVoucherEntity;
import kr.co.pincoin.api.infra.shop.mapper.product.VoucherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProductVoucherMapper {
  private final OrderProductMapper orderProductMapper;

  private final VoucherMapper voucherMapper;

  public OrderProductVoucher toModel(OrderProductVoucherEntity entity) {
    if (entity == null) {
      return null;
    }

    return OrderProductVoucher.builder()
        .id(entity.getId())
        .code(entity.getCode())
        .revoked(entity.getRevoked())
        .remarks(entity.getRemarks())
        .orderProduct(
            Optional.ofNullable(entity.getOrderProduct())
                .map(orderProductMapper::toModel)
                .orElse(null))
        .voucher(Optional.ofNullable(entity.getVoucher()).map(voucherMapper::toModel).orElse(null))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .isRemoved(entity.isRemoved())
        .build();
  }

  public OrderProductVoucherEntity toEntity(OrderProductVoucher model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<OrderProductVoucher> toModelList(List<OrderProductVoucherEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public List<OrderProductVoucherEntity> toEntityList(List<OrderProductVoucher> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
