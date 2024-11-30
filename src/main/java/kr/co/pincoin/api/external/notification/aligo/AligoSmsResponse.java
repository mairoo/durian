package kr.co.pincoin.api.external.notification.aligo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AligoSmsResponse {

  @JsonProperty("result_code")
  private String resultCode;

  @JsonProperty("message")
  private String message;

  @JsonProperty("msg_id")
  private String msgId;

  @JsonProperty("success_cnt")
  private String successCount;

  @JsonProperty("error_cnt")
  private String errorCount;

  @JsonProperty("msg_type")
  private String msgType;
}
