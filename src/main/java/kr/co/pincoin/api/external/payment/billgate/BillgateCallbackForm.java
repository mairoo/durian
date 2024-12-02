package kr.co.pincoin.api.external.payment.billgate;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JSON 역직렬화를 위한 기본 생성자
@AllArgsConstructor(access = AccessLevel.PRIVATE) // @Builder 사용 private 생성자
@Builder
public class BillgateCallbackForm {

  @NotBlank private String serviceCode;

  @NotBlank private String serviceId;

  @NotBlank private String orderId;

  @NotBlank private String orderDate;

  private String payMessage;
}
