package kr.co.pincoin.api.external.payment.billgate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillgateApprovalResponse {

  @JsonProperty("responseCode")
  private String responseCode;

  @JsonProperty("responseMessage")
  private String responseMessage;

  @JsonProperty("orderId")
  private String orderId;

  @JsonProperty("authAmount")
  private String authAmount;
}
