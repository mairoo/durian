package kr.co.pincoin.api.external.notification.line;

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
public class LineNotifyApiClient {

    private final WebClient lineNotifyWebClient;
    private final ObjectMapper objectMapper;

    @Value("${line-notify.token}")
    private String token;

    public LineNotifyApiClient(WebClient lineNotifyWebClient, ObjectMapper objectMapper) {
        this.lineNotifyWebClient = lineNotifyWebClient;
        this.objectMapper = objectMapper;
    }

    public Mono<LineNotifyResponse> sendNotification(LineNotifyRequest request) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("message", request.getMessage());

        return lineNotifyWebClient
            .post()
            .uri("/api/notify")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header("Authorization", "Bearer " + token)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(response -> {
                try {
                    LineNotifyResponse lineNotifyResponse =
                        objectMapper.readValue(response, LineNotifyResponse.class);
                    return Mono.just(lineNotifyResponse);
                } catch (JsonProcessingException e) {
                    return Mono.error(
                        new BusinessException(ErrorCode.LINE_NOTIFY_API_PARSE_ERROR, e));
                }
            })
            .onErrorMap(e -> {
                if (e instanceof BusinessException) {
                    return e;
                }
                return new BusinessException(ErrorCode.LINE_NOTIFY_API_SEND_ERROR, e);
            });
    }
}

