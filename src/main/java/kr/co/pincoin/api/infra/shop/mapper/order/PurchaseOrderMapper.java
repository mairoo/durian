package kr.co.pincoin.api.infra.shop.mapper.order;

import kr.co.pincoin.api.domain.shop.model.order.PurchaseOrder;
import kr.co.pincoin.api.infra.shop.entity.order.PurchaseOrderEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurchaseOrderMapper {
    public PurchaseOrder toModel(PurchaseOrderEntity entity) {
        if (entity == null) {
            return null;
        }

        return PurchaseOrder.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .paid(entity.getPaid())
                .bankAccount(entity.getBankAccount())
                .amount(entity.getAmount())
                .created(entity.getCreated())
                .modified(entity.getModified())
                .isRemoved(entity.getIsRemoved())
                .build();
    }

    public PurchaseOrderEntity toEntity(PurchaseOrder model) {
        if (model == null) {
            return null;
        }

        return model.toEntity();
    }

    public List<PurchaseOrder> toModelList(List<PurchaseOrderEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<PurchaseOrderEntity> toEntityList(List<PurchaseOrder> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}