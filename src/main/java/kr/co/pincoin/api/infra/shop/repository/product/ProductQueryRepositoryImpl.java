package kr.co.pincoin.api.infra.shop.repository.product;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.infra.shop.entity.product.ProductEntity;
import kr.co.pincoin.api.infra.shop.entity.product.QProductEntity;
import kr.co.pincoin.api.infra.shop.mapper.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {
  private final JPAQueryFactory queryFactory;

  private final ProductMapper productMapper;

  private final QProductEntity product = QProductEntity.productEntity;

  @Override
  public List<ProductEntity> findAllByCategory(Long categoryId, String categorySlug,
      ProductStatus status, ProductStock stock) {
    return queryFactory
        .selectFrom(product)
        .join(product.category).fetchJoin()
        .join(product.store).fetchJoin()
        .where(
            categoryIdEq(categoryId),
            categorySlugEq(categorySlug),
            statusEq(status),
            stockEq(stock)
        )
        .fetch();
  }

  private BooleanExpression categoryIdEq(Long categoryId) {
    return categoryId != null ? product.category.id.eq(categoryId) : null;
  }

  private BooleanExpression categorySlugEq(String categorySlug) {
    return categorySlug != null ? product.category.slug.eq(categorySlug) : null;
  }

  private BooleanExpression statusEq(ProductStatus status) {
    return status != null ? product.status.eq(status) : null;
  }

  private BooleanExpression stockEq(ProductStock stock) {
    return stock != null ? product.stock.eq(stock) : null;
  }
}
