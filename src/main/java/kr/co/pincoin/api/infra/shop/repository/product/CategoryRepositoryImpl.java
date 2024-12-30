package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.product.CategoryDetached;
import kr.co.pincoin.api.domain.shop.repository.product.CategoryRepository;
import kr.co.pincoin.api.infra.shop.mapper.product.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

  private final CategoryJpaRepository jpaRepository;
  private final CategoryQueryRepository queryRepository;
  private final CategoryMapper mapper;

  /**
   * 카테고리를 생성하거나 수정합니다
   *
   * @param category 저장할 카테고리
   * @return 저장된 카테고리
   */
  @Override
  public Category save(Category category) {
    return mapper.toModel(jpaRepository.save(mapper.toEntity(category)));
  }

  /**
   * ID로 카테고리를 조회합니다
   *
   * @param id 카테고리 ID
   * @return 조회된 카테고리 (없을 경우 Optional.empty)
   */
  @Override
  public Optional<Category> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toModel);
  }

  /**
   * Slug로 카테고리를 조회합니다
   *
   * @param slug 카테고리 슬러그
   * @return 조회된 카테고리 (없을 경우 Optional.empty)
   */
  @Override
  public Optional<Category> findBySlug(String slug) {
    return jpaRepository.findBySlug(slug).map(mapper::toModel);
  }

  /**
   * ID로 분리된 카테고리를 조회합니다
   *
   * @param id 카테고리 ID
   * @return 조회된 분리된 카테고리 (없을 경우 Optional.empty)
   */
  @Override
  public Optional<CategoryDetached> findDetachedById(Long id) {
    return queryRepository.findDetachedById(id);
  }

  /**
   * Slug로 분리된 카테고리를 조회합니다
   *
   * @param slug 카테고리 슬러그
   * @return 조회된 분리된 카테고리 (없을 경우 Optional.empty)
   */
  @Override
  public Optional<CategoryDetached> findDetachedBySlug(String slug) {
    return queryRepository.findDetachedBySlug(slug);
  }

  /**
   * 모든 카테고리를 조회합니다
   *
   * @return 카테고리 목록
   */
  @Override
  public List<Category> findAll() {
    return mapper.toModelList(jpaRepository.findAll());
  }

  /**
   * 특정 스토어의 모든 카테고리를 스토어 정보와 함께 조회합니다
   *
   * @param storeId 스토어 ID
   * @return 해당 스토어의 카테고리 목록
   */
  @Override
  public List<Category> findAllByStoreIdWithStore(Long storeId) {
    return mapper.toModelList(jpaRepository.findAllByStoreIdWithStore(storeId));
  }

  /**
   * 특정 부모 카테고리의 모든 하위 카테고리를 조회합니다
   *
   * @param parent 부모 카테고리
   * @return 하위 카테고리 목록
   */
  @Override
  public List<Category> findAllByParentCategory(Category parent) {
    return mapper.toModelList(jpaRepository.findAllByParentCategory(parent));
  }

  /**
   * 특정 스토어의 최상위 카테고리들을 조회합니다 (부모 카테고리가 없는 카테고리)
   *
   * @param storeId 스토어 ID
   * @return 최상위 카테고리 목록
   */
  @Override
  public List<Category> findAllByStoreIdAndParentCategoryIsNull(Long storeId) {
    return mapper.toModelList(jpaRepository.findAllByStoreIdAndParentCategoryIsNull(storeId));
  }

  /**
   * 카테고리를 삭제합니다
   *
   * @param category 삭제할 카테고리
   */
  @Override
  public void delete(Category category) {
    jpaRepository.delete(mapper.toEntity(category));
  }

  /**
   * ID로 카테고리를 삭제합니다
   *
   * @param id 삭제할 카테고리의 ID
   */
  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }
}
