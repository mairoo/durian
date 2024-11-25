package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherJpaRepository extends JpaRepository<VoucherEntity, Long> {
    @Query("SELECT v FROM VoucherEntity v " +
            "WHERE v.product.code = :productCode " +
            "AND v.status = :status " +
            "ORDER BY v.id ASC " +
            "LIMIT :limit")
    List<VoucherEntity>
    findTopNByProductCodeAndStatusOrderByIdAsc(String productCode,
                                               VoucherStatus status,
                                               int limit);
}
