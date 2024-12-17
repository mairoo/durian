package kr.co.pincoin.api.external.payment.banktransfer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kr.co.pincoin.api.domain.shop.model.order.enums.PaymentAccount;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PUBLIC) // ModelAttribute 바인딩을 위해 Setter 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JSON 역직렬화를 위한 기본 생성자
public class BankTransferRequest {

    /*
    curl -X POST \
      -H "Content-Type: application/x-www-form-urlencoded" \
      -H "Cache-Control: no-cache" \
      -H "Authorization: Token YOUR_PAYMENT_NOTIFY_TOKEN" \
      --data-urlencode "account=계좌번호" \
      --data-urlencode "received=수신시간" \
      --data-urlencode "name=입금자명" \
      --data-urlencode "method=결제방법" \
      --data-urlencode "amount=금액" \
      --data-urlencode "balance=잔액" \
     http://localhost:8080/payment/bank-transfer/callback
     */

    @NotNull(message = "입금계좌는 필수입니다.")
    private PaymentAccount account;

    @NotBlank(message = "입금시각은 필수입니다")
    private String received;

    @NotBlank(message = "입금자명은 필수입니다")
    private String name;

    @NotBlank(message = "입출금구분은 필수입니다.")
    private String method;

    @NotBlank(message = "입금금액은 필수입니다")
    @Pattern(regexp = "^[0-9,]+$", message = "입금금액은 숫자와 콤마만 가능합니다")
    private String amount;

    @NotBlank(message = "잔액은 필수입니다")
    @Pattern(regexp = "^[0-9,]+$", message = "잔액은 숫자와 콤마만 가능합니다")
    private String balance;
}