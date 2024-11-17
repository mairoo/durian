package kr.co.pincoin.api.infra.shop.mapper.store;

import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.infra.shop.entity.store.StoreEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StoreMapper {
    public Store toModel(StoreEntity entity) {
        if (entity == null) {
            return null;
        }

        return Store.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .theme(entity.getTheme())
                .phone(entity.getPhone())
                .phone1(entity.getPhone1())
                .kakao(entity.getKakao())
                .bankAccount(entity.getBankAccount())
                .escrowAccount(entity.getEscrowAccount())
                .chunkSize(entity.getChunkSize())
                .blockSize(entity.getBlockSize())
                .signupOpen(entity.getSignupOpen())
                .underAttack(entity.getUnderAttack())
                .created(entity.getCreated())
                .modified(entity.getModified())
                .build();
    }

    public StoreEntity toEntity(Store model) {
        if (model == null) {
            return null;
        }

        return model.toEntity();
    }

    public List<Store> toModelList(List<StoreEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<StoreEntity> toEntityList(List<Store> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}