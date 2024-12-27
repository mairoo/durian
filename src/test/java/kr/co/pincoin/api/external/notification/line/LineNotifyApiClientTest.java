package kr.co.pincoin.api.external.notification.line;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pincoin.api.global.config.WebClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {WebClientConfig.class, LineNotifyApiClient.class, ObjectMapper.class})
@ActiveProfiles("test")
class LineNotifyApiClientTest {

  @Autowired private LineNotifyApiClient lineNotifyApiClient;

  @Test
  void sendNotification() {
    // Given
    LineNotifyRequest request = LineNotifyRequest.builder().message("Line Notify 발송 테스트").build();

    // When
    LineNotifyResponse response = lineNotifyApiClient.sendNotification(request).block();

    // Then
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    assertEquals("ok", response.getMessage());
  }
}
