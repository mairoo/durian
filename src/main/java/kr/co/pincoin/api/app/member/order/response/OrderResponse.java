package kr.co.pincoin.api.app.member.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderPaymentMethod;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    @JsonProperty("id")
    private final Long id;

    @JsonProperty("orderNo")
    private final String orderNo;

    @JsonProperty("fullname")
    private final String fullname;

    @JsonProperty("totalListPrice")
    private final BigDecimal totalListPrice;

    @JsonProperty("totalSellingPrice")
    private final BigDecimal totalSellingPrice;

    @JsonProperty("currency")
    private final OrderCurrency currency;

    @JsonProperty("status")
    private final OrderStatus status;

    @JsonProperty("paymentMethod")
    private final OrderPaymentMethod paymentMethod;

    @JsonProperty("created")
    private final LocalDateTime created;

    @JsonProperty("modified")
    private final LocalDateTime modified;

    @JsonProperty("suspicious")
    private final Boolean suspicious;

    @JsonProperty("removed")
    private final Boolean removed;

    // 생성자 외부 접근 불허 / 자식 허용
    protected OrderResponse(Order order) {
        this.id = order.getId();
        this.orderNo = order.getOrderNo();
        this.fullname = order.getFullname();
        this.totalListPrice = order.getTotalListPrice();
        this.totalSellingPrice = order.getTotalSellingPrice();
        this.currency = order.getCurrency();
        this.status = order.getStatus();
        this.paymentMethod = order.getPaymentMethod();
        this.created = order.getCreated();
        this.modified = order.getModified();
        this.suspicious = order.getSuspicious();
        this.removed = order.getRemoved();
    }

    // 도메인 모델 객체에서 응답 객체 초기화
    public static OrderResponse from(Order order) {
        return new OrderResponse(order);
    }
}
