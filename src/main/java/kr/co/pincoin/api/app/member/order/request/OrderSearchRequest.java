package kr.co.pincoin.api.app.member.order.request;

import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor // GET 요청 쿼리 파라미터는 불필요하지만 코드 일관성
@AllArgsConstructor // GET 요청 쿼리 파라미터는 불필요하지만 코드 일관성
public class OrderSearchRequest {
    private String orderNo;

    private OrderStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
}