package kr.co.pincoin.api.infra.shop.repository.store;

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
}