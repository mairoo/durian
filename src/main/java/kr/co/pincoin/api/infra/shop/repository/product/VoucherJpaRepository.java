package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherJpaRepository extends JpaRepository<VoucherEntity, Long> {

  Optional<VoucherEntity> findByCode(String code);

  List<VoucherEntity> findAllByIdIn(Collection<Long> ids);

  List<VoucherEntity> findAllByCodeIn(Collection<String> codes);

  @Query(
      "SELECT v FROM VoucherEntity v "
          + "JOIN FETCH v.product p "
          + "JOIN FETCH p.category c "
          + "WHERE p.code = :productCode "
          + "AND v.status = :status "
          + "ORDER BY v.id")
  List<VoucherEntity> findAllByProductCodeAndStatus(
      String productCode, VoucherStatus status, Pageable pageable);
}
