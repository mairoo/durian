package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.domain.shop.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}