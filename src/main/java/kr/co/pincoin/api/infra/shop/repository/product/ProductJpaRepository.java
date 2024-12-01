package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.infra.shop.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
  Optional<ProductEntity> findByCode(String code);

  List<ProductEntity> findAllByIdIn(Collection<Long> ids);

  List<ProductEntity> findAllByCodeIn(Collection<String> codes);

  @Query("SELECT p FROM ProductEntity p JOIN FETCH p.category WHERE p.id = :id")
  Optional<ProductEntity> findByIdWithCategory(@Param("id") Long id);

  @Query("SELECT p FROM ProductEntity p JOIN FETCH p.category WHERE p.category.id = :categoryId")
  List<ProductEntity> findAllByCategoryIdWithCategory(@Param("categoryId") Long categoryId);
}
