package kr.co.pincoin.api.infra.auth.mapper.phone;

import kr.co.pincoin.api.domain.auth.model.phone.PhoneVerificationLog;
import kr.co.pincoin.api.infra.auth.entity.phone.PhoneVerificationLogEntity;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PhoneVerificationLogMapper {
    private final UserMapper userMapper;

    public PhoneVerificationLog toModel(PhoneVerificationLogEntity entity) {
        if (entity == null) {
            return null;
        }

        return PhoneVerificationLog.from(entity);
    }

    public PhoneVerificationLogEntity toEntity(PhoneVerificationLog model) {
        if (model == null) {
            return null;
        }

        return PhoneVerificationLogEntity.builder()
                .id(model.getId())
                .token(model.getToken())
                .code(model.getCode())
                .reason(model.getReason())
                .resultCode(model.getResultCode())
                .message(model.getMessage())
                .transactionId(model.getTransactionId())
                .di(model.getDi())
                .ci(model.getCi())
                .fullname(model.getFullname())
                .dateOfBirth(model.getDateOfBirth())
                .gender(model.getGender())
                .domestic(model.getDomestic())
                .telecom(model.getTelecom())
                .cellphone(model.getCellphone())
                .returnMessage(model.getReturnMessage())
                .owner(userMapper.toEntity(model.getOwner()))
                .build();
    }

    public List<PhoneVerificationLog> toModelList(List<PhoneVerificationLogEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<PhoneVerificationLogEntity> toEntityList(List<PhoneVerificationLog> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}