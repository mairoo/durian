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

  /**
   * 바우처 코드로 바우처를 조회합니다.
   *
   * @param code 조회할 바우처 코드
   * @return 조회된 바우처 엔티티
   */
  Optional<VoucherEntity> findByCode(String code);

  /**
   * ID 목록으로 바우처 목록을 조회합니다.
   *
   * @param ids 조회할 바우처 ID 목록
   * @return 조회된 바우처 엔티티 목록
   */
  List<VoucherEntity> findAllByIdIn(Collection<Long> ids);

  /**
   * 바우처 코드 목록으로 바우처 목록을 조회합니다.
   *
   * @param codes 조회할 바우처 코드 목록
   * @return 조회된 바우처 엔티티 목록
   */
  List<VoucherEntity> findAllByCodeIn(Collection<String> codes);

  /**
   * 상품 코드와 바우처 상태로 바우처 목록을 조회합니다. N+1 문제를 방지하기 위해 Projection을 사용하며, 비관적 락(SELECT FOR UPDATE SKIP
   * LOCKED)을 적용합니다. 락이 걸린 행은 제외하고 조회하여 대기 없이 처리합니다.
   *
   * @param productCode 조회할 상품 코드
   * @param status 조회할 바우처 상태
   * @param pageable 페이지네이션 정보
   * @return 조회된 바우처 정보 배열 목록
   */
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

  /**
   * 지정된 바우처 ID 목록의 상태를 일괄 변경합니다. 변경 후 영속성 컨텍스트를 자동으로 초기화합니다.
   *
   * @param voucherIds 상태를 변경할 바우처 ID 목록
   * @param status 변경할 바우처 상태
   */
  @Modifying(clearAutomatically = true)
  @Query("UPDATE VoucherEntity v SET v.status = :status WHERE v.id IN :voucherIds")
  void updateStatusToSold(
      @Param("voucherIds") List<Long> voucherIds, @Param("status") VoucherStatus status);
}
