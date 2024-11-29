package kr.co.pincoin.api.app.member.order.request;

import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JSON 역직렬화를 위한 기본 생성자
public class OrderSearchRequest {
  private String orderNo;

  private OrderStatus status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime endDate;
}
