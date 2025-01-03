package kr.co.pincoin.api.domain.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;

public interface ProductRepository {

  Product save(Product product);

  List<Product> saveAll(Collection<Product> products);

  Optional<Product> findById(Long id, ProductStatus status, ProductStock stock);

  Optional<Product> findByIdWithCategory(Long id);

  Optional<ProductDetached> findDetachedById(Long id, ProductStatus status, ProductStock stock);

  Optional<ProductDetached> findDetachedByCode(
      String code, ProductStatus status, ProductStock stock);

  List<Product> findAll();

  List<Product> findAllByIdIn(Collection<Long> ids);

  List<Product> findAllByCodeIn(Collection<String> codes);

  List<Product> findAllByCodeInWithCategory(List<String> codes);

  List<ProductDetached> findAllDetachedByCodeIn(
      Collection<String> codes, ProductStatus status, ProductStock stock);

  List<ProductDetached> findAllByCategory(
      Long categoryId, String categorySlug, ProductStatus status, ProductStock stock);

  void delete(Product product);

  void deleteById(Long id);

  void softDelete(Product product);

  void softDeleteById(Long id);

  void restore(Product product);

  void restoreById(Long id);

  void decreaseStockQuantity(String productCode, int quantity);
}
