package kr.co.pincoin.api.infra.shop.repository.product;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
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
  public Optional<ProductEntity> findById(Long id, ProductStatus status, ProductStock stock) {
    return Optional.ofNullable(queryFactory
        .selectFrom(product)
        .join(product.category).fetchJoin()
        .where(
            idEq(id),
            statusEq(status),
            stockEq(stock)
        ).fetchOne());
  }

  @Override
  public Optional<ProductDetached> findDetachedById(Long id, ProductStatus status,
      ProductStock stock) {
    return Optional.ofNullable(queryFactory
        .select(getProductDetachedProjection())
        .from(product)
        .join(product.category)
        .where(
            idEq(id),
            statusEq(status),
            stockEq(stock)
        )
        .fetchOne());
  }

  @Override
  public List<ProductDetached> findAllByCategory(Long categoryId, String categorySlug,
      ProductStatus status, ProductStock stock) {
    return queryFactory
        .select(getProductDetachedProjection())
        .from(product)
        .join(product.category)
        .where(
            categoryIdEq(categoryId),
            categorySlugEq(categorySlug),
            statusEq(status),
            stockEq(stock)
        )
        .fetch();
  }

  private Expression<ProductDetached> getProductDetachedProjection() {
    return Projections.constructor(ProductDetached.class,
        product.id,
        product.name,
        product.subtitle,
        product.code,
        product.pg,
        product.naverPartner,
        product.naverPartnerTitle,
        product.naverPartnerTitlePg,
        product.naverAttribute,
        product.created,
        product.modified,
        product.category.id,
        product.status,
        product.stock,
        product.listPrice,
        product.sellingPrice,
        product.pgSellingPrice,
        product.minimumStockLevel,
        product.maximumStockLevel,
        product.stockQuantity,
        product.description,
        product.position,
        product.reviewCount,
        product.reviewCountPg,
        product.isRemoved
    );
  }

  private BooleanExpression idEq(Long id) {
    return product.id.eq(id);
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
