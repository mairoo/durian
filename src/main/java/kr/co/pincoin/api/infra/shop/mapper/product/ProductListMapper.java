package kr.co.pincoin.api.infra.shop.mapper.product;

import kr.co.pincoin.api.domain.shop.model.product.ProductList;
import kr.co.pincoin.api.infra.shop.entity.product.ProductListEntity;
import kr.co.pincoin.api.infra.shop.mapper.store.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductListMapper {
    private final StoreMapper storeMapper;

    public ProductList toModel(ProductListEntity entity) {
        if (entity == null) {
            return null;
        }

        return ProductList.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .store(storeMapper.toModel(entity.getStore()))
                .created(entity.getCreated())
                .modified(entity.getModified())
                .build();
    }

    public ProductListEntity toEntity(ProductList model) {
        if (model == null) {
            return null;
        }

        return model.toEntity();
    }

    public List<ProductList> toModelList(List<ProductListEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<ProductListEntity> toEntityList(List<ProductList> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}