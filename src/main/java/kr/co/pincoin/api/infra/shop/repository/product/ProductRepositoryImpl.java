package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.infra.shop.mapper.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return productJpaRepository.findById(id)
                .map(productMapper::toModel);
    }

    @Override
    public Optional<Product> findByCode(String code) {
        return productJpaRepository.findByCode(code)
                .map(productMapper::toModel);
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
}