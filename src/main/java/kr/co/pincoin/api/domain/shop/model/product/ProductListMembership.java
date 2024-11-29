package kr.co.pincoin.api.domain.shop.model.product;

import kr.co.pincoin.api.infra.shop.entity.product.ProductListMembershipEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductListMembership {
  private final Long id;

  private final Product product;

  private final ProductList productList;

  private Integer position;

  @Builder
  private ProductListMembership(
      Long id, Integer position, Product product, ProductList productList) {
    this.id = id;
    this.position = position;
    this.product = product;
    this.productList = productList;

    validateMembership();
  }

  public ProductListMembershipEntity toEntity() {
    return ProductListMembershipEntity.builder()
        .id(this.getId())
        .position(this.getPosition())
        .product(this.getProduct().toEntity())
        .productList(this.getProductList().toEntity())
        .build();
  }

  public static ProductListMembership of(Product product, ProductList productList) {
    return ProductListMembership.builder().product(product).productList(productList).build();
  }

  public static ProductListMembership of(
      Product product, ProductList productList, Integer position) {
    return ProductListMembership.builder()
        .product(product)
        .productList(productList)
        .position(position)
        .build();
  }

  public void updatePosition(Integer position) {
    if (position == null || position < 0) {
      throw new IllegalArgumentException("Position must be non-negative");
    }
    this.position = position;
  }

  public void moveToTop() {
    this.position = 0;
  }

  public void moveToBottom(int maxPosition) {
    if (maxPosition < 0) {
      throw new IllegalArgumentException("Max position must be non-negative");
    }
    this.position = maxPosition + 1;
  }

  public void moveUp() {
    if (this.position > 0) {
      this.position--;
    }
  }

  public void moveDown() {
    this.position++;
  }

  public boolean belongsToStore(Long storeId) {
    return this.productList != null && this.productList.belongsToStore(storeId);
  }

  public boolean containsProduct(Long productId) {
    return this.product != null && this.product.getId().equals(productId);
  }

  private void validateMembership() {
    if (product == null) {
      throw new IllegalArgumentException("Product cannot be null");
    }
    if (productList == null) {
      throw new IllegalArgumentException("Product list cannot be null");
    }
    if (position < 0) {
      throw new IllegalArgumentException("Position must be non-negative");
    }

    // 상품과 상품 목록이 같은 스토어에 속하는지 검증
    if (!productList.belongsToStore(product.getStore().getId())) {
      throw new IllegalArgumentException("Product and product list must belong to the same store");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ProductListMembership that = (ProductListMembership) o;

    if (id != null && that.id != null) {
      return id.equals(that.id);
    }

    return product.getId().equals(that.product.getId())
        && productList.getId().equals(that.productList.getId());
  }

  @Override
  public int hashCode() {
    if (id != null) {
      return id.hashCode();
    }
    return 31 * product.getId().hashCode() + productList.getId().hashCode();
  }
}
