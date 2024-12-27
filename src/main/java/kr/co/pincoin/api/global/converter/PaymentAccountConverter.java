package kr.co.pincoin.api.global.converter;

import java.util.Arrays;
import kr.co.pincoin.api.domain.shop.model.order.enums.PaymentAccount;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PaymentAccountConverter implements Converter<String, PaymentAccount> {

  @Override
  public PaymentAccount convert(@NonNull String source) {
    if (source.trim().isEmpty()) {
      return null;
    }

    try {
      int value = Integer.parseInt(source);
      return Arrays.stream(PaymentAccount.values())
          .filter(pa -> pa.getValue().equals(value))
          .findFirst()
          .orElseThrow(
              () -> new IllegalArgumentException("Invalid payment account value: " + value));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Payment account must be a number");
    }
  }
}
