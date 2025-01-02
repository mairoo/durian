package kr.co.pincoin.api.infra.shop.mapper.product;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoucherMapper {
  private final ProductMapper productMapper;

  public Voucher toModel(VoucherEntity entity) {
    if (entity == null) {
      return null;
    }

    return Voucher.builder()
        .id(entity.getId())
        .code(entity.getCode())
        .remarks(entity.getRemarks())
        .status(entity.getStatus())
        .product(Optional.ofNullable(entity.getProduct()).map(productMapper::toModel).orElse(null))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .isRemoved(entity.isRemoved())
        .build();
  }

  public VoucherEntity toEntity(Voucher model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<Voucher> toModelList(List<VoucherEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public List<VoucherEntity> toEntityList(List<Voucher> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
