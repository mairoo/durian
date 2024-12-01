package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.infra.shop.entity.product.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
  Optional<CategoryEntity> findBySlug(String slug);

  @Query("SELECT c FROM CategoryEntity c JOIN FETCH c.store WHERE c.store.id = :storeId")
  List<CategoryEntity> findAllByStoreIdWithStore(@Param("storeId") Long storeId);

  @Query("SELECT c FROM CategoryEntity c WHERE c.parent = :parent")
  List<CategoryEntity> findAllByParentCategory(@Param("parent") Category parent);

  @Query(
      "SELECT c FROM CategoryEntity c JOIN FETCH c.store WHERE c.store.id = :storeId AND c.parent IS NULL")
  List<CategoryEntity> findAllByStoreIdAndParentCategoryIsNull(@Param("storeId") Long storeId);
}
