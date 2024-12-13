package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.infra.shop.mapper.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

  private final ProductJpaRepository jpaRepository;

  private final ProductQueryRepository queryRepository;

  private final ProductMapper mapper;

  @Override
  public Product save(Product product) {
    return mapper.toModel(jpaRepository.save(mapper.toEntity(product)));
  }

  @Override
  public List<Product> saveAll(Collection<Product> products) {
    return mapper.toModelList(
        jpaRepository.saveAll(mapper.toEntityList(products.stream().toList())));
  }

  @Override
  public Optional<Product> findByCode(String code) {
    return jpaRepository.findByCode(code).map(mapper::toModel);
  }

  @Override
  public List<Product> findAll() {
    return mapper.toModelList(jpaRepository.findAll());
  }

  @Override
  public List<Product> findAllByIdIn(Collection<Long> ids) {
    return jpaRepository.findAllByIdIn(ids).stream()
        .map(mapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Product> findAllByCodeIn(Collection<String> codes) {
    return jpaRepository.findAllByCodeIn(codes).stream()
        .map(mapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Product> findByIdWithCategory(Long id) {
    return jpaRepository.findByIdWithCategory(id).map(mapper::toModel);
  }

  @Override
  public List<ProductDetached> findAllByCategory(Long categoryId, String categorySlug,
      ProductStatus status, ProductStock stock) {
    return queryRepository.findAllByCategory(categoryId, categorySlug, status, stock);
  }

  @Override
  public void delete(Product product) {
    jpaRepository.delete(mapper.toEntity(product));
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public Optional<Product> findById(Long id, ProductStatus status, ProductStock stock) {
    return queryRepository.findById(id, status, stock).map(mapper::toModel);
  }

  @Override
  public void softDelete(Product product) {
    Product deletedProduct =
        findById(product.getId(), null, null)
            .map(
                p -> {
                  p.softDelete();
                  return p;
                })
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    save(deletedProduct);
  }

  @Override
  public void softDeleteById(Long id) {
    findById(id, null, null)
        .map(
            p -> {
              p.softDelete();
              return save(p);
            })
        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
  }

  @Override
  public void restore(Product product) {
    Product restoredProduct =
        findById(product.getId(), null, null)
            .map(
                p -> {
                  p.restore();
                  return p;
                })
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    save(restoredProduct);
  }

  @Override
  public void restoreById(Long id) {
    findById(id, null, null)
        .map(
            p -> {
              p.restore();
              return save(p);
            })
        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
  }
}
