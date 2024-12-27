package kr.co.pincoin.api.external.notification.line;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LineNotifyResponse {

  private Integer status;
  private String message;

  @JsonProperty("status")
  public void setStatus(Integer status) {
    this.status = status;
  }

  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }
}
