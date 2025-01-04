package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherJpaRepository extends JpaRepository<VoucherEntity, Long> {

  Optional<VoucherEntity> findByCode(String code);

  List<VoucherEntity> findAllByIdIn(Collection<Long> ids);

  List<VoucherEntity> findAllByCodeIn(Collection<String> codes);

  // N+1 문제
  // VoucherEntity 조회 시 Product를 JOIN FETCH 즉시 로딩(eager loading)할 경우
  // Product 엔티티가 Category까지 함께 로딩할 수 있는 가능성을 차단하기 위해 Projection 사용
  //
  // 비관적 락: SELECT FOR UPDATE SKIP LOCKED
  // 락 걸린 행은 제외하고 가지고 와서 대기 없이 처리
  @Query(
      value =
          """
    SELECT
        v.id,
        v.code,
        v.remarks,
        v.created,
        v.modified,
        v.status,
        v.is_removed
    FROM shop_voucher v
    INNER JOIN shop_product p ON v.product_id = p.id
    WHERE p.code = :productCode
    AND v.status = :status
    ORDER BY v.id
    LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}
    FOR UPDATE SKIP LOCKED
    """,
      nativeQuery = true)
  List<Object[]> findAllByProductCodeAndStatus(
      @Param("productCode") String productCode,
      @Param("status") VoucherStatus status,
      Pageable pageable);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE VoucherEntity v SET v.status = :status WHERE v.id IN :voucherIds")
  void updateStatusToSold(
      @Param("voucherIds") List<Long> voucherIds, @Param("status") VoucherStatus status);
}
