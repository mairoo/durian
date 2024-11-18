package kr.co.pincoin.api.infra.shop.repository.store;

import kr.co.pincoin.api.domain.shop.model.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreJpaRepository extends JpaRepository<Store, Long> {
}