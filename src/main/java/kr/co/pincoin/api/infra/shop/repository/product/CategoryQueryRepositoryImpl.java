package kr.co.pincoin.api.infra.shop.repository.product;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.CategoryDetached;
import kr.co.pincoin.api.infra.shop.entity.product.QCategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository {
  private final JPAQueryFactory queryFactory;
  private final QCategoryEntity category = QCategoryEntity.categoryEntity;

  @Override
  public Optional<CategoryDetached> findDetachedById(Long id) {
    return Optional.ofNullable(
        queryFactory
            .select(getCategoryDetachedProjection())
            .from(category)
            .where(idEq(id))
            .fetchOne());
  }

  @Override
  public Optional<CategoryDetached> findDetachedBySlug(String slug) {
    return Optional.ofNullable(
        queryFactory
            .select(getCategoryDetachedProjection())
            .from(category)
            .where(slugEq(slug))
            .fetchOne());
  }

  private Expression<CategoryDetached> getCategoryDetachedProjection() {
    return Projections.constructor(
        CategoryDetached.class,
        category.id,
        category.title,
        category.slug,
        category.pg,
        category.parent.id,
        category.store.id,
        category.created,
        category.modified,
        category.thumbnail,
        category.description,
        category.description1,
        category.discountRate,
        category.pgDiscountRate,
        category.naverSearchTag,
        category.naverBrandName,
        category.naverMakerName,
        category.lft,
        category.rght,
        category.treeId,
        category.level);
  }

  private BooleanExpression idEq(Long id) {
    return id != null ? category.id.eq(id) : null;
  }

  private BooleanExpression slugEq(String slug) {
    return slug != null ? category.slug.eq(slug) : null;
  }
}
