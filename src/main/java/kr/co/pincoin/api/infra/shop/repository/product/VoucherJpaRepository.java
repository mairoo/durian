package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
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
          + "WHERE v.product.code = :productCode "
          + "AND v.status = :status "
          + "ORDER BY v.id ASC "
          + "LIMIT :limit")
  List<VoucherEntity> findTopNByProductCodeAndStatusOrderByIdAsc(
      String productCode, VoucherStatus status, int limit);

  @Query(
      "SELECT v FROM VoucherEntity v "
          + "JOIN FETCH v.product p "
          + "JOIN FETCH p.category c "
          + "WHERE p.code IN :productCodes "
          + "AND v.status = :status "
          + "ORDER BY p.code, v.id")
  List<VoucherEntity> findAllByProductCodesAndStatus(
      Collection<String> productCodes, VoucherStatus status);
}
