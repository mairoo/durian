package kr.co.pincoin.api.infra.shop.mapper.product;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.converter.VoucherStatusConverter;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import kr.co.pincoin.api.infra.shop.repository.product.projection.VoucherProjection;
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

  public List<Voucher> toModelList(List<VoucherEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public VoucherEntity toEntity(Voucher model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<VoucherEntity> toEntityList(List<Voucher> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }

  public VoucherProjection toProjection(Object[] result) {
    return new VoucherProjection(
        ((Number) result[0]).longValue(), // id (bigint)
        (String) result[1], // code (varchar)
        (String) result[2], // remarks (varchar)
        ((Timestamp) result[3]).toLocalDateTime(), // created (datetime)
        ((Timestamp) result[4]).toLocalDateTime(), // modified (datetime)
        VoucherStatusConverter.fromOrdinal((Number) result[5]), // status (int)
        (Boolean) result[6] // is_removed (tinyint)
        );
  }

  public List<VoucherProjection> toProjectionList(List<Object[]> results) {
    if (results == null) {
      return Collections.emptyList();
    }

    return results.stream().map(this::toProjection).collect(Collectors.toList());
  }
}
