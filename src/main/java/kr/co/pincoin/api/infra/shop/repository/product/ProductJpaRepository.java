package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.infra.shop.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
  List<ProductEntity> saveAll(Collection<ProductEntity> products);

  Optional<ProductEntity> findByCode(String code);

  List<ProductEntity> findAllByIdIn(Collection<Long> ids);

  List<ProductEntity> findAllByCodeIn(Collection<String> codes);
}
