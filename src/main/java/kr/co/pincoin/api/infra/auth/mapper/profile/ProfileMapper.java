package kr.co.pincoin.api.infra.auth.mapper.profile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.infra.auth.entity.profile.ProfileEntity;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileMapper {
  private final UserMapper userMapper;

  public Profile toModel(ProfileEntity entity) {
    if (entity == null) {
      return null;
    }

    return Profile.builder()
        .id(entity.getId())
        .user(userMapper.toModel(entity.getUser()))
        .phone(entity.getPhone())
        .address(entity.getAddress())
        .phoneVerified(entity.getPhoneVerified())
        .phoneVerifiedStatus(entity.getPhoneVerifiedStatus())
        .documentVerified(entity.getDocumentVerified())
        .allowOrder(entity.getAllowOrder())
        .photoId(entity.getPhotoId())
        .card(entity.getCard())
        .totalOrderCount(entity.getTotalOrderCount())
        .firstPurchased(entity.getFirstPurchased())
        .lastPurchased(entity.getLastPurchased())
        .notPurchasedMonths(entity.getNotPurchasedMonths())
        .repurchased(entity.getRepurchased())
        .maxPrice(entity.getMaxPrice())
        .totalListPrice(entity.getTotalListPrice())
        .totalSellingPrice(entity.getTotalSellingPrice())
        .averagePrice(entity.getAveragePrice())
        .mileage(entity.getMileage())
        .memo(entity.getMemo())
        .dateOfBirth(entity.getDateOfBirth())
        .gender(entity.getGender())
        .domestic(entity.getDomestic())
        .telecom(entity.getTelecom())
        .created(entity.getCreated())
        .modified(entity.getModified())
        .build();
  }

  public List<Profile> toModelList(List<ProfileEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public ProfileEntity toEntity(Profile model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<ProfileEntity> toEntityList(List<Profile> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
