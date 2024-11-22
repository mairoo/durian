package kr.co.pincoin.api.domain.shop.model.product;

import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.infra.shop.entity.product.ProductListEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductList {
    private final Long id;

    private String name;

    private final String code;

    private final Store store;

    private final LocalDateTime created;

    private final LocalDateTime modified;

    @Builder
    private ProductList(Long id, String name, String code, Store store,
                        LocalDateTime created, LocalDateTime modified) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.store = store;
        this.created = created;
        this.modified = modified;

        validateProductList();
    }

    public ProductListEntity toEntity() {
        return ProductListEntity.builder()
                .id(this.getId())
                .name(this.getName())
                .code(this.getCode())
                .store(this.getStore().toEntity())
                .build();
    }

    public static ProductList of(String name, String code, Store store) {
        return ProductList.builder()
                .name(name)
                .code(code)
                .store(store)
                .build();
    }

    public void
    updateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product list name cannot be empty");
        }
        this.name = name;
    }

    public boolean
    belongsToStore(Long storeId) {
        return this.store != null &&
                this.store.getId().equals(storeId);
    }

    private void
    validateProductList() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product list name cannot be empty");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Product list code cannot be empty");
        }
        if (!code.matches("^[A-Za-z0-9-]+$")) {
            throw new IllegalArgumentException("Product list code must contain only letters, numbers, and hyphens");
        }
        if (store == null) {
            throw new IllegalArgumentException("Store is required");
        }
    }
}
