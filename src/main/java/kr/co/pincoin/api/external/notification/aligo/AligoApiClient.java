package kr.co.pincoin.api.external.notification.aligo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    formData.add("msg", request.getMsg());
    formData.add("key", key);
    formData.add("user_id", userId);
    formData.add("sender", sender);

    return aligoWebClient.post()
        .uri("/send/")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(formData))
        .retrieve()
        .bodyToMono(String.class)
        .map(response -> {
          try {
            return objectMapper.readValue(response, AligoSmsResponse.class);
          } catch (JsonProcessingException e) {
            log.error("응답 파싱 중 에러 발생: {}", response, e);
            throw new RuntimeException("알리고 API 응답 파싱 실패", e);
          }
        });
  }
}
