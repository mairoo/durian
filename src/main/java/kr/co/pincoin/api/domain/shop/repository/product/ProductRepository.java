package kr.co.pincoin.api.domain.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;

public interface ProductRepository {

  Product save(Product product);

  List<Product> saveAll(Collection<Product> products);

  void delete(Product product);

  void deleteById(Long id);

  Optional<Product> findById(Long id, ProductStatus status, ProductStock stock);

  Optional<Product> findByCode(String code);

  List<Product> findAll();

  List<Product> findAllByIdIn(Collection<Long> ids);

  List<Product> findAllByCodeIn(Collection<String> codes);

  Optional<Product> findByIdWithCategory(Long id);

  List<Product> findAllByCategory(Long categoryId, String categorySlug,
      ProductStatus status, ProductStock stock);

  void softDelete(Product product);

  void softDeleteById(Long id);

  void restore(Product product);

  void restoreById(Long id);
}
