package kr.co.pincoin.api.domain.shop.repository.store;

import kr.co.pincoin.api.domain.shop.model.store.Store;

import java.util.Optional;

public interface StoreRepository {
    Optional<Store> findById(Long id);

    Optional<Store> findByCode(String code);
}