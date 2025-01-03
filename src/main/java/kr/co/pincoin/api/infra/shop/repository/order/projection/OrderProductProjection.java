package kr.co.pincoin.api.infra.shop.repository.order.projection;

import kr.co.pincoin.api.infra.auth.entity.profile.ProfileEntity;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;

public record OrderProductProjection(
    OrderProductEntity orderProduct, OrderEntity order, UserEntity user, ProfileEntity profile) {}
