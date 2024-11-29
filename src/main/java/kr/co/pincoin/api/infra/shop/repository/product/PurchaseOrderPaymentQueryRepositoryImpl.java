package kr.co.pincoin.api.infra.shop.repository.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PurchaseOrderPaymentQueryRepositoryImpl
    implements PurchaseOrderPaymentQueryRepository {
  private final JPAQueryFactory queryFactory;
}
