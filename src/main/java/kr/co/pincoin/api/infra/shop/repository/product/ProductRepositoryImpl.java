package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.infra.shop.mapper.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
  private final ProductJpaRepository productJpaRepository;

  private final ProductQueryRepository productQueryRepository;

  private final ProductMapper productMapper;

  @Override
  public Product save(Product product) {
    return productMapper.toModel(productJpaRepository.save(productMapper.toEntity(product)));
  }

  @Override
  public Optional<Product> findById(Long id) {
    return productJpaRepository.findById(id).map(productMapper::toModel);
  }

  @Override
  public Optional<Product> findByCode(String code) {
    return productJpaRepository.findByCode(code).map(productMapper::toModel);
  }

  @Override
  public List<Product> findAll() {
    return productMapper.toModelList(productJpaRepository.findAll());
  }

  @Override
  public List<Product> findAllByIdIn(Collection<Long> ids) {
    return productJpaRepository.findAllByIdIn(ids).stream()
        .map(productMapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Product> findAllByCodeIn(Collection<String> codes) {
    return productJpaRepository.findAllByCodeIn(codes).stream()
        .map(productMapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsBySlug(String code) {
    return productJpaRepository.findByCode(code).isPresent();
  }

  @Override
  public void delete(Product product) {
    productJpaRepository.delete(productMapper.toEntity(product));
  }

  @Override
  public void deleteById(Long id) {
    productJpaRepository.deleteById(id);
  }

  @Override
  public void softDelete(Product product) {
    Product deletedProduct =
        findById(product.getId())
            .map(
                p -> {
                  p.softDelete();
                  return p;
                })
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    save(deletedProduct);
  }

  @Override
  public void softDeleteById(Long id) {
    findById(id)
        .map(
            p -> {
              p.softDelete();
              return save(p);
            })
        .orElseThrow(() -> new IllegalArgumentException("Product not found"));
  }

  @Override
  public void restore(Product product) {
    Product restoredProduct =
        findById(product.getId())
            .map(
                p -> {
                  p.restore();
                  return p;
                })
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    save(restoredProduct);
  }

  @Override
  public void restoreById(Long id) {
    findById(id)
        .map(
            p -> {
              p.restore();
              return save(p);
            })
        .orElseThrow(() -> new IllegalArgumentException("Product not found"));
  }
}
