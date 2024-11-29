package kr.co.pincoin.api.app.member.order.service;

import kr.co.pincoin.api.app.member.order.request.OrderCreateRequest;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.global.utils.ClientUtils;
import kr.co.pincoin.api.infra.shop.service.OrderPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderPersistenceService orderPersistenceService;

    public Page<Order> getMyOrders(User user, OrderSearchCondition condition, Pageable pageable) {
        return orderPersistenceService.searchUserOrders(user.getId(), condition, pageable);
    }

    public Order getMyOrder(User user, String orderNo) {
        return orderPersistenceService.findUserOrder(user.getId(), orderNo);
    }

    @Transactional
    public void deleteMyOrder(User user, String orderNo) {
        orderPersistenceService.softDeleteUserOrder(user.getId(), orderNo);
    }

    @Transactional
    public void hideMyOrder(User user, String orderNo) {
        orderPersistenceService.hideUserOrder(user.getId(), orderNo);
    }

    @Transactional
    public Order createOrder(OrderCreateRequest request, User user, ClientUtils.ClientInfo clientInfo) {
        return orderPersistenceService.createOrder(user, request, clientInfo);
    }

    @Transactional
    public Order reorder(User user, String orderNo, ClientUtils.ClientInfo clientInfo) {
        return orderPersistenceService.createReorder(user.getId(), orderNo, clientInfo);
    }

    @Transactional
    public Order requestRefund(User user, String message, String orderNo) {
        Order order = orderPersistenceService.findUserOrder(user.getId(), orderNo);
        return orderPersistenceService.requestRefund(user, order, message);
    }
}
