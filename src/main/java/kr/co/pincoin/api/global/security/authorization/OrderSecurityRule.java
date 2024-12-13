package kr.co.pincoin.api.global.security.authorization;

import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSecurityRule {

    private final OrderRepository orderRepository;

    public boolean hasOrderAccess(User user, String orderNo) {
        Integer userId = orderRepository.findUserIdByOrderNo(orderNo)
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        return userId.equals(user.getId());
    }

    public boolean hasOrderAccess(User user, Long orderId) {
        Integer userId = orderRepository.findUserIdById(orderId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        return userId.equals(user.getId());
    }
}
