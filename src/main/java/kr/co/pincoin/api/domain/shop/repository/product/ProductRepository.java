package kr.co.pincoin.api.domain.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Product;

public interface ProductRepository {

  Product save(Product product);

  List<Product> saveAll(Collection<Product> products);

  void delete(Product product);

  void deleteById(Long id);

  Optional<Product> findById(Long id);

  Optional<Product> findByCode(String code);

  List<Product> findAll();

  List<Product> findAllByIdIn(Collection<Long> ids);

  List<Product> findAllByCodeIn(Collection<String> codes);

  boolean existsBySlug(String code);

  void softDelete(Product product);

  void softDeleteById(Long id);

  void restore(Product product);

  void restoreById(Long id);
}
