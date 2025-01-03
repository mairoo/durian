package kr.co.pincoin.api.infra.shop.repository.product;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.entity.product.QProductEntity;
import kr.co.pincoin.api.infra.shop.entity.product.QVoucherEntity;
import kr.co.pincoin.api.infra.shop.repository.product.projection.ProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.product.projection.VoucherProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoucherQueryRepositoryImpl implements VoucherQueryRepository {
  private final JPAQueryFactory queryFactory;

  // N+1 문제 가능성
  // VoucherEntity를 조회하면서 Product를 JOIN FETCH 즉시 로딩(eager loading)하면
  // Product 엔티티가 Category와 연관관계가 있는 경우 이것도 함께 로딩
  @Override
  public List<VoucherProjection> findAllByProductCodeAndStatus(
      String productCode, VoucherStatus status, Pageable pageable) {
    QVoucherEntity voucher = QVoucherEntity.voucherEntity;

    return queryFactory
        .select(
            Projections.constructor(
                VoucherProjection.class,
                voucher.id,
                voucher.code,
                voucher.remarks,
                voucher.created,
                voucher.modified,
                voucher.status,
                voucher.isRemoved))
        .from(voucher)
        .where(eqProductCode(voucher, productCode), eqVoucherStatus(voucher, status))
        .orderBy(voucher.id.asc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }

  @Override
  public List<ProductVoucherCount> countByProductCodesAndStatus(
      List<String> productCodes, VoucherStatus status) {
    QProductEntity product = QProductEntity.productEntity;
    QVoucherEntity voucher = QVoucherEntity.voucherEntity;

    return queryFactory
        .select(
            Projections.constructor(
                ProductVoucherCount.class, product.code, voucher.count().as("availableCount")))
        .from(voucher)
        .join(product)
        .on(voucher.product.id.eq(product.id))
        .where(product.code.in(productCodes), eqVoucherStatus(voucher, status))
        .groupBy(product.code)
        .fetch();
  }

  private BooleanExpression eqProductCode(QVoucherEntity voucher, String productCode) {
    return voucher.product.code.eq(productCode);
  }

  private BooleanExpression eqVoucherStatus(QVoucherEntity voucher, VoucherStatus status) {
    return voucher.status.eq(status);
  }
}
