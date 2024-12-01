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

  private final StoreJpaRepository jpaRepository;

  private final StoreQueryRepository queryRepository;

  private final StoreMapper mapper;

  @Override
  public Optional<Store> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toModel);
  }

  @Override
  public Optional<Store> findByCode(String code) {
    return jpaRepository.findByCode(code).map(mapper::toModel);
  }
}
