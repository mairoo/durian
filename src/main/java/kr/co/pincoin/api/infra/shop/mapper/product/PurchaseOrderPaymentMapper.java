package kr.co.pincoin.api.infra.shop.mapper.product;

import kr.co.pincoin.api.domain.shop.model.product.PurchaseOrderPayment;
import kr.co.pincoin.api.infra.shop.entity.product.PurchaseOrderPaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PurchaseOrderPaymentMapper {
    private final PurchaseOrderMapper purchaseOrderMapper;

    public PurchaseOrderPayment toModel(PurchaseOrderPaymentEntity entity) {
        if (entity == null) {
            return null;
        }

        return PurchaseOrderPayment.builder()
                .id(entity.getId())
                .account(entity.getAccount())
                .amount(entity.getAmount())
                .order(purchaseOrderMapper.toModel(entity.getOrder()))
                .created(entity.getCreated())
                .modified(entity.getModified())
                .isRemoved(entity.isRemoved())
                .build();
    }

    public PurchaseOrderPaymentEntity toEntity(PurchaseOrderPayment model) {
        if (model == null) {
            return null;
        }

        return model.toEntity();
    }

    public List<PurchaseOrderPayment> toModelList(List<PurchaseOrderPaymentEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<PurchaseOrderPaymentEntity> toEntityList(List<PurchaseOrderPayment> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}