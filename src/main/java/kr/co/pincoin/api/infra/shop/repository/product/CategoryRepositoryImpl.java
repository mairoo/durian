package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.repository.product.CategoryRepository;
import kr.co.pincoin.api.infra.shop.mapper.product.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
    private final CategoryJpaRepository categoryJpaRepository;

    private final CategoryQueryRepository categoryQueryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public Category save(Category category) {
        return categoryMapper.toModel(categoryJpaRepository.save(categoryMapper.toEntity(category)));
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryJpaRepository.findById(id)
                .map(categoryMapper::toModel);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return categoryJpaRepository.findBySlug(slug)
                .map(categoryMapper::toModel);
    }

    @Override
    public List<Category> findAll() {
        return categoryMapper.toModelList(categoryJpaRepository.findAll());
    }

    @Override
    public boolean existsBySlug(String slug) {
        return categoryJpaRepository.findBySlug(slug).isPresent();
    }

    @Override
    public void delete(Category category) {
        categoryJpaRepository.delete(categoryMapper.toEntity(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryJpaRepository.deleteById(id);
    }
}