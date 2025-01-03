package kr.co.pincoin.api.infra.shop.repository.order;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.infra.shop.entity.order.QOrderProductEntity;
import kr.co.pincoin.api.infra.shop.entity.order.QOrderProductVoucherEntity;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductVoucherQueryRepositoryImpl implements OrderProductVoucherQueryRepository {
  private final JPAQueryFactory queryFactory;

  public List<OrderProductVoucherProjection> findAllByOrderProductOrderId(Long orderId) {
    QOrderProductVoucherEntity orderProductVoucher =
        QOrderProductVoucherEntity.orderProductVoucherEntity;
    QOrderProductEntity orderProduct = QOrderProductEntity.orderProductEntity;

    return queryFactory
        .select(
            Projections.constructor(
                OrderProductVoucherProjection.class,
                orderProduct.name,
                orderProduct.subtitle,
                orderProductVoucher.code,
                orderProductVoucher.remarks,
                orderProductVoucher.revoked))
        .from(orderProductVoucher)
        .join(orderProductVoucher.orderProduct, orderProduct)
        .where(eqOrderId(orderProduct, orderId))
        .fetch();
  }

  @Override
  public List<OrderProductVoucherCount> countIssuedVouchersByOrderProducts(
      List<OrderProduct> orderProducts) {
    QOrderProductVoucherEntity orderProductVoucher =
        QOrderProductVoucherEntity.orderProductVoucherEntity;
    QOrderProductEntity orderProduct = QOrderProductEntity.orderProductEntity;

    List<Long> orderProductIds =
        orderProducts.stream().map(OrderProduct::getId).collect(Collectors.toList());

    return queryFactory
        .select(
            Projections.constructor(
                OrderProductVoucherCount.class,
                orderProduct.code,
                orderProductVoucher.count().as("issuedCount")))
        .from(orderProductVoucher)
        .join(orderProduct)
        .on(orderProductVoucher.orderProduct.id.eq(orderProduct.id))
        .where(inOrderProductIds(orderProduct, orderProductIds), isNotRevoked(orderProductVoucher))
        .groupBy(orderProduct.code)
        .fetch();
  }

  private BooleanExpression eqOrderId(QOrderProductEntity product, Long orderId) {
    return product.order.id.eq(orderId);
  }

  private BooleanExpression inOrderProductIds(
      QOrderProductEntity orderProduct, List<Long> orderProductIds) {
    return orderProduct.id.in(orderProductIds);
  }

  private BooleanExpression isNotRevoked(QOrderProductVoucherEntity orderProductVoucher) {
    return orderProductVoucher.revoked.eq(false);
  }
}
