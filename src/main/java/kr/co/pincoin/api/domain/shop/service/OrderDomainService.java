package kr.co.pincoin.api.domain.shop.service;

import jakarta.persistence.EntityNotFoundException;
import kr.co.pincoin.api.app.member.order.request.OrderCreateRequest;
import kr.co.pincoin.api.app.member.order.request.OrderLineItem;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.global.utils.ClientUtils;
import kr.co.pincoin.api.infra.shop.service.OrderPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderDomainService {
    private final OrderPersistenceService persistenceService;
    private final OrderDomainServiceHelper helper;

    /**
     * 주문 조회
     */
    public Page<Order> getOrders(OrderSearchCondition condition, Pageable pageable) {
        return persistenceService.searchOrders(condition, pageable);
    }

    public Order getOrder(Long orderId) {
        return persistenceService.findOrder(orderId);
    }

    public Page<Order> getUserOrders(Integer userId, OrderSearchCondition condition, Pageable pageable) {
        return persistenceService.searchUserOrders(userId, condition, pageable);
    }

    public Order getUserOrder(Integer userId, String orderNo) {
        return persistenceService.findUserOrder(userId, orderNo);
    }

    public User getUser(Integer userId) {
        return persistenceService.findUser(userId);
    }

    /**
     * 주문 생성
     */
    @Transactional
    public Order createOrder(User user, OrderCreateRequest request, ClientUtils.ClientInfo clientInfo) {
        List<Product> products = helper.validateProductsForOrder(request.getItems());

        BigDecimal totalListPrice = helper.calculateTotalPrice(products, request.getItems(), Product::getListPrice);
        BigDecimal totalSellingPrice = helper.calculateTotalPrice(products, request.getItems(),
                                                                  Product::getSellingPrice);

        Order order = Order.builder()
                .orderNo(helper.generateOrderNumber())
                .fullname(user.getLastName() + user.getFirstName())
                .userAgent(clientInfo.getUserAgent())
                .acceptLanguage(clientInfo.getAcceptLanguage())
                .ipAddress(clientInfo.getIpAddress())
                .totalListPrice(totalListPrice)
                .totalSellingPrice(totalSellingPrice)
                .currency(OrderCurrency.KRW)
                .parent(null)
                .user(user)
                .paymentMethod(request.getPaymentMethod())
                .status(OrderStatus.PAYMENT_PENDING)
                .visibility(OrderVisibility.VISIBLE)
                .transactionId("")
                .message("")
                .suspicious(false)
                .removed(false)
                .build();

        Order savedOrder = persistenceService.save(order);

        List<OrderProduct> orderProducts = helper.createOrderProducts(products, request.getItems(), savedOrder);
        persistenceService.saveOrderProducts(orderProducts);

        return savedOrder;
    }

    @Transactional
    public Order createReorder(Integer userId, String orderNo, ClientUtils.ClientInfo clientInfo) {
        List<OrderProduct> originalOrderProducts = persistenceService.findOriginalOrderProducts(orderNo, userId);
        if (originalOrderProducts.isEmpty()) {
            throw new EntityNotFoundException("주문을 찾을 수 없습니다.");
        }

        Order originalOrder = originalOrderProducts.getFirst().getOrder();

        List<OrderLineItem> orderItems = originalOrderProducts.stream()
                .map(op -> new OrderLineItem(op.getCode(), op.getQuantity()))
                .collect(Collectors.toList());
        helper.validateProductsForOrder(orderItems);

        Order reorder = Order.builder()
                .orderNo(helper.generateOrderNumber())
                .fullname(originalOrder.getFullname())
                .userAgent(clientInfo.getUserAgent())
                .acceptLanguage(clientInfo.getAcceptLanguage())
                .ipAddress(clientInfo.getIpAddress())
                .totalListPrice(originalOrder.getTotalListPrice())
                .totalSellingPrice(originalOrder.getTotalSellingPrice())
                .currency(originalOrder.getCurrency())
                .user(originalOrder.getUser())
                .paymentMethod(originalOrder.getPaymentMethod())
                .status(OrderStatus.PAYMENT_PENDING)
                .visibility(OrderVisibility.VISIBLE)
                .transactionId("")
                .message("")
                .suspicious(false)
                .removed(false)
                .build();

        Order savedReorder = persistenceService.save(reorder);

        List<OrderProduct> newOrderProducts = helper.copyOrderProducts(originalOrderProducts, savedReorder);
        persistenceService.saveOrderProducts(newOrderProducts);

        return savedReorder;
    }

    /**
     * 주문 처리
     */
    @Transactional
    public OrderPayment addPayment(Long orderId, OrderPayment payment) {
        Order order = persistenceService.findOrder(orderId);
        OrderPayment savedPayment = persistenceService.savePayment(payment);

        BigDecimal totalPayments = helper.calculateTotalPayments(order);

        if (helper.isPaymentCompleted(totalPayments, order.getTotalSellingPrice())) {
            Profile profile = persistenceService.findUserProfile(order.getUser());
            helper.updateOrderStatusAfterPayment(order, profile);
            persistenceService.save(order);
        }

        return savedPayment;
    }

    @Transactional
    public Order issueVouchers(Order order) {
        List<OrderProduct> orderProducts = persistenceService.findOrderProducts(order);

        for (OrderProduct orderProduct : orderProducts) {
            Product product = helper.validateProductForVoucherIssue(orderProduct);

            List<Voucher> vouchers = persistenceService.findAvailableVouchers(
                    orderProduct.getCode(),
                    orderProduct.getQuantity());

            helper.validateVouchersAvailability(orderProduct, vouchers, product);

            List<OrderProductVoucher> orderProductVouchers = new ArrayList<>();
            for (Voucher voucher : vouchers) {
                voucher.markAsSold();
                orderProductVouchers.add(OrderProductVoucher.builder()
                                                 .orderProduct(orderProduct)
                                                 .code(voucher.getCode())
                                                 .remarks(voucher.getRemarks())
                                                 .revoked(false)
                                                 .build());
            }

            persistenceService.saveOrderProductVouchers(orderProductVouchers);

            product.updateStockQuantity(product.getStockQuantity() - orderProduct.getQuantity());
            persistenceService.updateProduct(product);
        }

        return order;
    }

    /**
     * 주문 상태 관리
     */
    @Transactional
    public void verifyOrder(Order order) {
        if (order.getStatus() != OrderStatus.PAYMENT_COMPLETED &&
                order.getStatus() != OrderStatus.UNDER_REVIEW) {
            throw new IllegalStateException("결제 완료 또는 검토중 상태의 주문만 검증할 수 있습니다.");
        }

        order.updateStatus(OrderStatus.PAYMENT_VERIFIED);
        persistenceService.save(order);
    }

    @Transactional
    public void unverifyOrder(Order order) {
        if (order.getStatus() != OrderStatus.PAYMENT_VERIFIED) {
            throw new IllegalStateException("검증 완료 상태의 주문만 변경할 수 있습니다.");
        }

        order.updateStatus(OrderStatus.UNDER_REVIEW);
        persistenceService.save(order);
    }

    @Transactional
    public void softDeleteOrder(Long orderId) {
        Order order = persistenceService.findOrder(orderId);
        if (Boolean.TRUE.equals(order.getRemoved())) {
            throw new IllegalStateException("이미 삭제된 주문입니다.");
        }
        persistenceService.softDeleteOrder(order);
    }

    @Transactional
    public void softDeleteUserOrder(Integer userId, String orderNo) {
        Order order = persistenceService.findUserOrder(userId, orderNo);
        if (Boolean.TRUE.equals(order.getRemoved())) {
            throw new IllegalStateException("이미 삭제된 주문입니다.");
        }
        persistenceService.softDeleteUserOrder(order);
    }

    @Transactional
    public void hideOrder(Long orderId) {
        Order order = persistenceService.findOrder(orderId);
        if (OrderVisibility.HIDDEN.equals(order.getVisibility())) {
            throw new IllegalStateException("이미 숨김 처리된 주문입니다.");
        }
        persistenceService.hideOrder(order);
    }

    @Transactional
    public void hideUserOrder(Integer userId, String orderNo) {
        Order order = persistenceService.findUserOrder(userId, orderNo);
        if (OrderVisibility.HIDDEN.equals(order.getVisibility())) {
            throw new IllegalStateException("이미 숨김 처리된 주문입니다.");
        }
        persistenceService.hideUserOrder(order);
    }

    /**
     * 환불 처리
     */
    @Transactional
    public Order requestRefund(User user, Order order, String message) {
        helper.validateRefundRequest(order);

        order.updateStatus(OrderStatus.REFUND_REQUESTED);
        order.updateMessage(message);
        persistenceService.save(order);

        Order refundOrder = Order.builder()
                .orderNo(helper.generateOrderNumber())
                .fullname(order.getFullname())
                .userAgent(order.getUserAgent())
                .acceptLanguage(order.getAcceptLanguage())
                .ipAddress(order.getIpAddress())
                .totalListPrice(order.getTotalListPrice())
                .totalSellingPrice(order.getTotalSellingPrice())
                .currency(order.getCurrency())
                .parent(order)
                .user(user)
                .paymentMethod(order.getPaymentMethod())
                .status(OrderStatus.REFUND_PENDING)
                .visibility(OrderVisibility.VISIBLE)
                .transactionId("")
                .message(message)
                .suspicious(false)
                .removed(false)
                .build();

        Order savedRefundOrder = persistenceService.save(refundOrder);

        List<OrderProduct> originalOrderProducts = persistenceService.findOrderProducts(order);
        List<OrderProduct> refundOrderProducts = helper.copyOrderProducts(originalOrderProducts, savedRefundOrder);
        persistenceService.saveOrderProducts(refundOrderProducts);

        helper.revokeVouchers(order.getId());

        return savedRefundOrder;
    }

    @Transactional
    public Order completeRefund(Order refundOrder) {
        helper.validateRefundCompletion(refundOrder);

        Order originalOrder = refundOrder.getParent();
        if (originalOrder == null) {
            throw new IllegalStateException("환불 처리할 원본 주문을 찾을 수 없습니다.");
        }

        originalOrder.updateStatus(OrderStatus.REFUNDED1);
        refundOrder.updateStatus(OrderStatus.REFUNDED2);

        persistenceService.save(originalOrder);
        persistenceService.save(refundOrder);

        return refundOrder;
    }
}