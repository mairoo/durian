package kr.co.pincoin.api.infra.shop.mapper.order;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductVoucherEntity;
import kr.co.pincoin.api.infra.shop.mapper.product.VoucherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProductVoucherMapper {
  private final OrderProductMapper orderProductMapper;

  private final VoucherMapper voucherMapper;

  // 일반 조회용
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

  // 벌크 저장용
  public OrderProductVoucher toModel(
      OrderProductVoucherEntity entity,
      OrderProduct originalOrderProduct,
      Voucher originalVoucher) {
    if (entity == null) {
      return null;
    }

    return OrderProductVoucher.builder()
        .id(entity.getId())
        .code(entity.getCode())
        .revoked(entity.getRevoked())
        .remarks(entity.getRemarks())

        // N+1 문제 방지
        //
        // OrderProductVoucherEntity를 저장한 후 다시 모델로 변환할 때, OrderProduct와 Voucher 정보가 필요합니다
        // 만약 원본 객체들을 전달하지 않으면, 각 OrderProductVoucher에 대해 연관된 OrderProduct와 Voucher를 개별적으로 조회해야 합니다
        // 예를 들어 100개의 바우처를 저장하면, 100번의 OrderProduct 조회 + 100번의 Voucher 조회가 추가로 발생할 수 있습니다
        .orderProduct(originalOrderProduct)
        .voucher(originalVoucher)
        .created(entity.getCreated())
        .modified(entity.getModified())
        .isRemoved(entity.isRemoved())
        .build();
  }

  // 일반 조회용
  public List<OrderProductVoucher> toModelList(List<OrderProductVoucherEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  // 벌크 저장용
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
                    // Mapper에서 추가 쿼리 없이 메모리에 있는 데이터 사용
                    originalOrderProducts.get(entity.getOrderProduct().getId()),
                    originalVouchers.get(entity.getVoucher().getId())))
        .collect(Collectors.toList());
  }

  public OrderProductVoucherEntity toEntity(OrderProductVoucher model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<OrderProductVoucherEntity> toEntityList(List<OrderProductVoucher> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
