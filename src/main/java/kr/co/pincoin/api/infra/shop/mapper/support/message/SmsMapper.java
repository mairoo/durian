package kr.co.pincoin.api.infra.shop.mapper.support.message;

import kr.co.pincoin.api.domain.shop.model.support.message.Sms;
import kr.co.pincoin.api.infra.shop.entity.support.message.SmsEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SmsMapper {
    public Sms toModel(SmsEntity entity) {
        if (entity == null) {
            return null;
        }

        return Sms.builder()
                .id(entity.getId())
                .phoneFrom(entity.getPhoneFrom())
                .phoneTo(entity.getPhoneTo())
                .content(entity.getContent())
                .success(entity.getSuccess())
                .created(entity.getCreated())
                .modified(entity.getModified())
                .build();
    }

    public SmsEntity toEntity(Sms model) {
        if (model == null) {
            return null;
        }

        return model.toEntity();
    }

    public List<Sms> toModelList(List<SmsEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<SmsEntity> toEntityList(List<Sms> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}