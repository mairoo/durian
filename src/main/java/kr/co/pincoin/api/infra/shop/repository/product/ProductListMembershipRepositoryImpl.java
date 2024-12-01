package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.domain.shop.repository.product.ProductListMembershipRepository;
import kr.co.pincoin.api.infra.shop.mapper.product.ProductListMembershipMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductListMembershipRepositoryImpl implements ProductListMembershipRepository {

  private final ProductListMembershipJpaRepository jpaRepository;

  private final ProductListMembershipQueryRepository queryRepository;

  private final ProductListMembershipMapper mapper;
}
