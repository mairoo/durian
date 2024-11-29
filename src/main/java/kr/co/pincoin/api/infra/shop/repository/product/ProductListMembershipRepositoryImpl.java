package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.domain.shop.repository.product.ProductListMembershipRepository;
import kr.co.pincoin.api.infra.shop.mapper.product.ProductListMembershipMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductListMembershipRepositoryImpl implements ProductListMembershipRepository {
  private final ProductListMembershipJpaRepository productListMembershipJpaRepository;

  private final ProductListMembershipQueryRepository productListMembershipQueryRepository;

  private final ProductListMembershipMapper productListMembershipMapper;
}
