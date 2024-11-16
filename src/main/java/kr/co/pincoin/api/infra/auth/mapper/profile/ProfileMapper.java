package kr.co.pincoin.api.infra.auth.mapper.profile;

import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.infra.auth.entity.profile.ProfileEntity;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfileMapper {
    private final UserMapper userMapper;

    public Profile toModel(ProfileEntity entity) {
        if (entity == null) {
            return null;
        }

        return Profile.from(entity);
    }

    public ProfileEntity toEntity(Profile model) {
        if (model == null) {
            return null;
        }

        return ProfileEntity.builder()
                .id(model.getId())
                .phone(model.getPhone())
                .address(model.getAddress())
                .phoneVerified(model.isPhoneVerified())
                .phoneVerifiedStatus(model.getPhoneVerifiedStatus())
                .documentVerified(model.isDocumentVerified())
                .allowOrder(model.isAllowOrder())
                .photoId(model.getPhotoId())
                .card(model.getCard())
                .totalOrderCount(model.getTotalOrderCount())
                .firstPurchased(model.getFirstPurchased())
                .lastPurchased(model.getLastPurchased())
                .notPurchasedMonths(model.isNotPurchasedMonths())
                .repurchased(model.getRepurchased())
                .maxPrice(model.getMaxPrice())
                .totalListPrice(model.getTotalListPrice())
                .totalSellingPrice(model.getTotalSellingPrice())
                .averagePrice(model.getAveragePrice())
                .mileage(model.getMileage())
                .memo(model.getMemo())
                .dateOfBirth(model.getDateOfBirth())
                .gender(model.getGender())
                .domestic(model.getDomestic())
                .telecom(model.getTelecom())
                .user(userMapper.toEntity(model.getUser()))
                .build();
    }

    public List<Profile> toModelList(List<ProfileEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<ProfileEntity> toEntityList(List<Profile> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}