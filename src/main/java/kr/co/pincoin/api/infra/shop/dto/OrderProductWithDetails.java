package kr.co.pincoin.api.infra.shop.dto;

import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.infra.auth.mapper.profile.ProfileMapper;
import kr.co.pincoin.api.infra.auth.mapper.user.UserMapper;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderMapper;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderProductMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderProductWithDetails {

  private final OrderProduct orderProduct;
  private final Order order;
  private final User user;
  private final Profile profile;

  public OrderProductWithDetails(
      OrderProductProjection projection,
      OrderProductMapper orderProductMapper,
      OrderMapper orderMapper,
      UserMapper userMapper,
      ProfileMapper profileMapper) {

    this.orderProduct = orderProductMapper.toModel(projection.getOrderProduct());
    this.order = orderMapper.toModel(projection.getOrder());
    this.user = userMapper.toModel(projection.getUser());
    this.profile = profileMapper.toModel(projection.getProfile());
  }
}
