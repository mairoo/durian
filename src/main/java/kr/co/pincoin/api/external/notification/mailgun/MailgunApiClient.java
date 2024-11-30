package kr.co.pincoin.api.external.notification.mailgun;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MailgunApiClient {

  private final WebClient mailgunWebClient;

  private final ObjectMapper objectMapper;

  @Value("${mailgun.domain}")
  private String domain;

  @Value("${mailgun.from}")
  private String from;

  public MailgunApiClient(WebClient mailgunWebClient, ObjectMapper objectMapper) {
    this.mailgunWebClient = mailgunWebClient;
    this.objectMapper = objectMapper;
  }

  public Mono<MailgunResponse> sendEmail(MailgunRequest request) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

    formData.add("from", from);
    formData.add("to", request.getTo());
    formData.add("subject", request.getSubject());
    formData.add("text", request.getText());

    if (request.getHtml() != null) {
      formData.add("html", request.getHtml());
    }

    return mailgunWebClient
        .post()
        .uri("/v3/" + domain + "/messages")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(formData))
        .retrieve()
        .bodyToMono(String.class)
        .flatMap(
            response -> {
              try {
                MailgunResponse mailgunResponse =
                    objectMapper.readValue(response, MailgunResponse.class);
                return Mono.just(mailgunResponse);
              } catch (JsonProcessingException e) {
                return Mono.error(new BusinessException(ErrorCode.MAILGUN_API_PARSE_ERROR, e));
              }
            })
        .onErrorMap(
            e -> {
              if (e instanceof BusinessException) {
                return e;
              }
              return new BusinessException(ErrorCode.MAILGUN_API_SEND_ERROR, e);
            });
  }
}
