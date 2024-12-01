package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Category;
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

  @Override
  public Category save(Category category) {
    return mapper.toModel(jpaRepository.save(mapper.toEntity(category)));
  }

  @Override
  public Optional<Category> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toModel);
  }

  @Override
  public Optional<Category> findBySlug(String slug) {
    return jpaRepository.findBySlug(slug).map(mapper::toModel);
  }

  @Override
  public List<Category> findAll() {
    return mapper.toModelList(jpaRepository.findAll());
  }

  @Override
  public boolean existsBySlug(String slug) {
    return jpaRepository.findBySlug(slug).isPresent();
  }

  @Override
  public void delete(Category category) {
    jpaRepository.delete(mapper.toEntity(category));
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }
}
