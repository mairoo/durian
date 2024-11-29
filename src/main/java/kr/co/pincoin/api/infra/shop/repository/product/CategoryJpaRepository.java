package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.Optional;
import kr.co.pincoin.api.infra.shop.entity.product.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
  Optional<CategoryEntity> findBySlug(String slug);
}
