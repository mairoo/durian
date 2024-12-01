package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.domain.shop.repository.product.ProductListRepository;
import kr.co.pincoin.api.infra.shop.mapper.product.ProductListMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductListRepositoryImpl implements ProductListRepository {

  private final ProductListJpaRepository jpaRepository;

  private final ProductListQueryRepository queryRepository;

  private final ProductListMapper mapper;
}
