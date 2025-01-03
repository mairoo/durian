package kr.co.pincoin.api.infra.shop.repository.product;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.entity.product.QProductEntity;
import kr.co.pincoin.api.infra.shop.entity.product.QVoucherEntity;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import kr.co.pincoin.api.infra.shop.repository.product.projection.ProductVoucherCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoucherQueryRepositoryImpl implements VoucherQueryRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<VoucherEntity> findAllByProductCodesAndVoucherStatus(
      List<String> productCodes, VoucherStatus status) {

    QProductEntity product = QProductEntity.productEntity;
    QVoucherEntity voucher = QVoucherEntity.voucherEntity;

    return queryFactory
        .selectFrom(voucher)
        .join(voucher.product, product)
        .where(product.code.in(productCodes), eqVoucherStatus(voucher, status))
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

  private BooleanExpression eqVoucherStatus(QVoucherEntity voucher, VoucherStatus status) {
    return voucher.status.eq(status);
  }
}
