package kr.co.pincoin.api.infra.shop.repository.store;

import java.util.Optional;
import kr.co.pincoin.api.infra.shop.entity.store.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreJpaRepository extends JpaRepository<StoreEntity, Long> {
  Optional<StoreEntity> findByCode(String code);
}
