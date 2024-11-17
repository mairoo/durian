package kr.co.pincoin.api.infra.auth.mapper.phone;

import kr.co.pincoin.api.domain.auth.model.phone.BannedPhone;
import kr.co.pincoin.api.infra.auth.entity.phone.BannedPhoneEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BannedPhoneMapper {
    public BannedPhone toModel(BannedPhoneEntity entity) {
        if (entity == null) {
            return null;
        }

        return BannedPhone.builder()
                .id(entity.getId())
                .phone(entity.getPhone())
                .created(entity.getCreated())
                .modified(entity.getModified())
                .isRemoved(entity.getIsRemoved())
                .build();
    }

    public BannedPhoneEntity toEntity(BannedPhone model) {
        if (model == null) {
            return null;
        }

        return model.toEntity();
    }

    public List<BannedPhone> toModelList(List<BannedPhoneEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<BannedPhoneEntity> toEntityList(List<BannedPhone> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}