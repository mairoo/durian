package kr.co.pincoin.api.domain.shop.repository.product;

import kr.co.pincoin.api.domain.shop.model.product.Product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAllByIdIn(Collection<Long> ids);
}