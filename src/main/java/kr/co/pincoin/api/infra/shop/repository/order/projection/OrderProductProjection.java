package kr.co.pincoin.api.infra.shop.repository.order.projection;

import kr.co.pincoin.api.infra.auth.entity.profile.ProfileEntity;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderProductProjection {

  private OrderProductEntity orderProduct;
  private OrderEntity order;
  private UserEntity user;
  private ProfileEntity profile;
}
