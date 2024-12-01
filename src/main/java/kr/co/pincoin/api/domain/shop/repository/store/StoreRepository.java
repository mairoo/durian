package kr.co.pincoin.api.domain.shop.repository.store;

import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.store.Store;

public interface StoreRepository {
  Optional<Store> findById(Long id);

  Optional<Store> findByCode(String code);

  boolean existsById(Long id);
}
