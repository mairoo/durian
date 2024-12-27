package kr.co.pincoin.api.infra.shop.repository.order;

import static com.querydsl.core.types.Projections.constructor;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.co.pincoin.api.infra.shop.entity.order.QOrderProductEntity;
import kr.co.pincoin.api.infra.shop.entity.order.QOrderProductVoucherEntity;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductVoucherQueryRepositoryImpl implements OrderProductVoucherQueryRepository {
  private final JPAQueryFactory queryFactory;

  public List<OrderProductVoucherProjection> findAllByOrderProductOrderId(Long orderId) {
    QOrderProductVoucherEntity voucher = QOrderProductVoucherEntity.orderProductVoucherEntity;
    QOrderProductEntity product = QOrderProductEntity.orderProductEntity;

    return queryFactory
        .select(
            constructor(
                OrderProductVoucherProjection.class,
                product.name,
                product.subtitle,
                voucher.code,
                voucher.remarks,
                voucher.revoked))
        .from(voucher)
        .join(voucher.orderProduct, product)
        .where(product.order.id.eq(orderId))
        .fetch();
  }
}
