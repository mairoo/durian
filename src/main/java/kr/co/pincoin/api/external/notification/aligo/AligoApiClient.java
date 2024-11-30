package kr.co.pincoin.api.external.notification.aligo;

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
public class AligoApiClient {

  private final WebClient aligoWebClient;

  private final ObjectMapper objectMapper;

  @Value("${aligo.key}")
  private String key;

  @Value("${aligo.user-id}")
  private String userId;

  @Value("${aligo.sender}")
  private String sender;

  public AligoApiClient(WebClient aligoWebClient, ObjectMapper objectMapper) {
    this.aligoWebClient = aligoWebClient;

    this.objectMapper = objectMapper;
  }

  public Mono<AligoSmsResponse> sendSms(AligoSmsRequest request) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

    formData.add("receiver", request.getReceiver());
    formData.add("msg", request.getMessage());
    formData.add("key", key);
    formData.add("user_id", userId);
    formData.add("sender", sender);

    return aligoWebClient
        .post()
        .uri("/send/")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(formData))
        .retrieve()
        .bodyToMono(String.class)
        .flatMap(
            response -> {
              try {
                AligoSmsResponse aligoResponse =
                    objectMapper.readValue(response, AligoSmsResponse.class);
                return Mono.just(aligoResponse);
              } catch (JsonProcessingException e) {
                return Mono.error(new BusinessException(ErrorCode.ALIGO_API_PARSE_ERROR, e));
              }
            })
        .onErrorMap(
            e -> {
              if (e instanceof BusinessException) {
                return e;
              }

              return new BusinessException(ErrorCode.ALIGO_API_SEND_ERROR, e);
            });
  }
}
