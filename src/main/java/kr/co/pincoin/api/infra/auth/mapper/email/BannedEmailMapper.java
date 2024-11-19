package kr.co.pincoin.api.infra.auth.mapper.email;

import kr.co.pincoin.api.domain.auth.model.email.BannedEmail;
import kr.co.pincoin.api.infra.auth.entity.email.BannedEmailEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BannedEmailMapper {
    public BannedEmail toModel(BannedEmailEntity entity) {
        if (entity == null) {
            return null;
        }

        return BannedEmail.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .created(entity.getCreated())
                .modified(entity.getModified())
                .isRemoved(entity.isRemoved())
                .build();
    }

    public BannedEmailEntity toEntity(BannedEmail model) {
        if (model == null) {
            return null;
        }

        return model.toEntity();
    }

    public List<BannedEmail> toModelList(List<BannedEmailEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<BannedEmailEntity> toEntityList(List<BannedEmail> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}