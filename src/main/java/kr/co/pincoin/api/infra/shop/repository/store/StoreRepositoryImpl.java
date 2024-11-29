package kr.co.pincoin.api.infra.shop.repository.store;

import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.domain.shop.repository.store.StoreRepository;
import kr.co.pincoin.api.infra.shop.mapper.store.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {
  private final StoreJpaRepository storeJpaRepository;

  private final StoreQueryRepository storeQueryRepository;

  private final StoreMapper storeMapper;

  @Override
  public Optional<Store> findById(Long id) {
    return storeJpaRepository.findById(id).map(storeMapper::toModel);
  }

  @Override
  public Optional<Store> findByCode(String code) {
    return storeJpaRepository.findByCode(code).map(storeMapper::toModel);
  }
}
