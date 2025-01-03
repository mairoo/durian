package kr.co.pincoin.api.infra.auth.mapper.phone;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.auth.model.phone.PhoneVerificationLog;
import kr.co.pincoin.api.infra.auth.entity.phone.PhoneVerificationLogEntity;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PhoneVerificationLogMapper {
  private final UserMapper userMapper;

  public PhoneVerificationLog toModel(PhoneVerificationLogEntity entity) {
    if (entity == null) {
      return null;
    }

    return PhoneVerificationLog.builder()
        .id(entity.getId())
        .token(entity.getToken())
        .code(entity.getCode())
        .reason(entity.getReason())
        .resultCode(entity.getResultCode())
        .message(entity.getMessage())
        .transactionId(entity.getTransactionId())
        .di(entity.getDi())
        .ci(entity.getCi())
        .fullname(entity.getFullname())
        .dateOfBirth(entity.getDateOfBirth())
        .gender(entity.getGender())
        .domestic(entity.getDomestic())
        .telecom(entity.getTelecom())
        .cellphone(entity.getCellphone())
        .returnMessage(entity.getReturnMessage())
        .owner(userMapper.toModel(entity.getOwner()))
        .created(entity.getCreated())
        .modified(entity.getModified())
        .build();
  }

  public List<PhoneVerificationLog> toModelList(List<PhoneVerificationLogEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public PhoneVerificationLogEntity toEntity(PhoneVerificationLog model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<PhoneVerificationLogEntity> toEntityList(List<PhoneVerificationLog> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
