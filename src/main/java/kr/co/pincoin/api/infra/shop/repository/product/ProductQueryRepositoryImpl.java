package kr.co.pincoin.api.infra.shop.repository.product;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
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

  /**
   * ID로 상품을 조회합니다
   *
   * @param id 상품 ID
   * @param status 상품 상태
   * @param stock 재고 상태
   * @return 조회된 상품 엔티티 (없을 경우 Optional.empty)
   */
  @Override
  public Optional<ProductEntity> findById(Long id, ProductStatus status, ProductStock stock) {
    return Optional.ofNullable(
        queryFactory
            .selectFrom(product)
            .join(product.category)
            .fetchJoin()
            .where(idEq(id), statusEq(status), stockEq(stock))
            .fetchOne());
  }

  /**
   * 코드로 분리된 상품을 조회합니다
   *
   * @param code 상품 코드
   * @param status 상품 상태
   * @param stock 재고 상태
   * @return 조회된 분리된 상품 (없을 경우 Optional.empty)
   */
  @Override
  public Optional<ProductDetached> findDetachedByCode(
      String code, ProductStatus status, ProductStock stock) {
    return Optional.ofNullable(
        queryFactory
            .select(getProductDetachedProjection())
            .from(product)
            .join(product.category)
            .where(codeEq(code), statusEq(status), stockEq(stock))
            .fetchOne());
  }

  /**
   * ID로 분리된 상품을 조회합니다
   *
   * @param id 상품 ID
   * @param status 상품 상태
   * @param stock 재고 상태
   * @return 조회된 분리된 상품 (없을 경우 Optional.empty)
   */
  @Override
  public Optional<ProductDetached> findDetachedById(
      Long id, ProductStatus status, ProductStock stock) {
    return Optional.ofNullable(
        queryFactory
            .select(getProductDetachedProjection())
            .from(product)
            .join(product.category)
            .where(idEq(id), statusEq(status), stockEq(stock))
            .fetchOne());
  }

  /**
   * 카테고리별 상품 목록을 조회합니다
   *
   * @param categoryId 카테고리 ID
   * @param categorySlug 카테고리 슬러그
   * @param status 상품 상태
   * @param stock 재고 상태
   * @return 카테고리에 속한 상품 목록
   */
  @Override
  public List<ProductDetached> findAllByCategory(
      Long categoryId, String categorySlug, ProductStatus status, ProductStock stock) {
    return queryFactory
        .select(getProductDetachedProjection())
        .from(product)
        .join(product.category)
        .where(
            categoryIdEq(categoryId),
            categorySlugEq(categorySlug),
            statusEq(status),
            stockEq(stock))
        .fetch();
  }

  /**
   * 여러 상품 코드로 분리된 상품 목록을 조회합니다
   *
   * @param codes 상품 코드 목록
   * @param status 상품 상태
   * @param stock 재고 상태
   * @return 조회된 분리된 상품 목록
   */
  @Override
  public List<ProductDetached> findAllDetachedByCodeIn(
      Collection<String> codes, ProductStatus status, ProductStock stock) {
    if (codes == null || codes.isEmpty()) {
      return List.of();
    }

    return queryFactory
        .select(getProductDetachedProjection())
        .from(product)
        .join(product.category)
        .where(product.code.in(codes), statusEq(status), stockEq(stock))
        .fetch();
  }

  private Expression<ProductDetached> getProductDetachedProjection() {
    return Projections.constructor(
        ProductDetached.class,
        product.id,
        product.code,
        product.name,
        product.subtitle,
        product.pg,
        product.naverPartner,
        product.naverPartnerTitle,
        product.naverPartnerTitlePg,
        product.naverAttribute,
        product.created,
        product.modified,
        product.category.id,
        product.category.slug,
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
        product.isRemoved);
  }

  private BooleanExpression idEq(Long id) {
    return product.id.eq(id);
  }

  private BooleanExpression codeEq(String code) {
    return product.code.eq(code);
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
