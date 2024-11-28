package kr.co.pincoin.api.domain.shop.model.order.condition;

import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderSearchCondition {
    private final Integer userId;
    private final String fullname;
    private final String orderNo;
    private final String phone;
    private final String email;
    private final String username;
    private final OrderStatus status;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    @Builder
    private OrderSearchCondition(Integer userId,
                                 String fullname,
                                 String orderNo,
                                 String phone,
                                 String email,
                                 String username,
                                 OrderStatus status,
                                 LocalDateTime startDate,
                                 LocalDateTime endDate) {
        this.userId = userId;
        this.fullname = fullname;
        this.orderNo = orderNo;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;

        validate();
    }

    // 정적 팩토리 메서드들
    public static OrderSearchCondition empty() {
        return OrderSearchCondition.builder().build();
    }

    public static OrderSearchCondition ofUserId(Integer userId) {
        return OrderSearchCondition.builder()
                .userId(userId)
                .build();
    }

    public static OrderSearchCondition ofOrderNo(String orderNo) {
        return OrderSearchCondition.builder()
                .orderNo(orderNo)
                .build();
    }

    public static OrderSearchCondition ofUser(String fullname, String phone, String email, String username) {
        return OrderSearchCondition.builder()
                .fullname(fullname)
                .phone(phone)
                .email(email)
                .username(username)
                .build();
    }

    public static OrderSearchCondition ofPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return OrderSearchCondition.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public static OrderSearchCondition ofStatus(OrderStatus status) {
        return OrderSearchCondition.builder()
                .status(status)
                .build();
    }

    // 기존 조건에 userId를 추가한 새로운 OrderSearchCondition을 반환하는 메서드
    public OrderSearchCondition withUserId(Integer userId) {
        return OrderSearchCondition.builder()
                .userId(userId)
                .fullname(this.fullname)
                .orderNo(this.orderNo)
                .phone(this.phone)
                .email(this.email)
                .username(this.username)
                .status(this.status)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .build();
    }

    private void validate() {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일이 종료일보다 늦을 수 없습니다.");
        }
    }

    // 유틸리티 메서드들
    public boolean hasUserId() {
        return userId != null;
    }

    public boolean hasDateRange() {
        return startDate != null && endDate != null;
    }

    public boolean hasOrderNo() {
        return orderNo != null && !orderNo.isBlank();
    }

    public boolean hasUserInfo() {
        return (fullname != null && !fullname.isBlank()) ||
                (phone != null && !phone.isBlank()) ||
                (email != null && !email.isBlank()) ||
                (username != null && !username.isBlank());
    }

    public boolean hasStatus() {
        return status != null;
    }
}