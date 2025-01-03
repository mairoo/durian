package kr.co.pincoin.api.infra.shop.mapper.order;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductVoucherEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProductVoucherMapper {
  public OrderProductVoucher toModel(
      OrderProductVoucherEntity entity, OrderProduct orderProduct, Voucher voucher) {
    if (entity == null) {
      return null;
    }

    return OrderProductVoucher.builder()
        .id(entity.getId())
        .code(entity.getCode())
        .revoked(entity.getRevoked())
        .remarks(entity.getRemarks())
        .orderProduct(orderProduct)
        .voucher(voucher)
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

  public List<OrderProductVoucher> toModelList(
      List<OrderProductVoucherEntity> entities,
      Map<Long, OrderProduct> originalOrderProducts,
      Map<Long, Voucher> originalVouchers) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream()
        .map(
            entity ->
                toModel(
                    entity,
                    originalOrderProducts.get(entity.getOrderProduct().getId()),
                    originalVouchers.get(entity.getVoucher().getId())))
        .collect(Collectors.toList());
  }

  public List<OrderProductVoucherEntity> toEntityList(List<OrderProductVoucher> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
