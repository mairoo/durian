package kr.co.pincoin.api.app.member.payment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JSON 역직렬화를 위한 기본 생성자
public class BankTransferRequest {

    @NotBlank(message = "계좌번호는 필수입니다")
    private String account;

    @NotBlank(message = "입금시각은 필수입니다")
    private String received;

    @NotBlank(message = "입금자명은 필수입니다")
    private String name;

    @NotBlank(message = "입금방법은 필수입니다")
    private String method;

    @NotBlank(message = "입금금액은 필수입니다")
    @Pattern(regexp = "^[0-9]+$", message = "입금금액은 숫자만 가능합니다")
    private String amount;

    @NotBlank(message = "잔액은 필수입니다")
    @Pattern(regexp = "^[0-9]+$", message = "잔액은 숫자만 가능합니다")
    private String balance;

    // 유효성 검사 메서드
    public void validateCallback() {
        if (amount == null || !amount.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Invalid amount format");
        }
        if (balance == null || !balance.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Invalid balance format");
        }
    }
}