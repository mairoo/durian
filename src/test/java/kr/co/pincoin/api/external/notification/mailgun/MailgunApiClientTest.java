package kr.co.pincoin.api.external.notification.mailgun;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pincoin.api.global.config.WebClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {WebClientConfig.class, MailgunApiClient.class, ObjectMapper.class})
@ActiveProfiles("test")
class MailgunApiClientTest {

  @Autowired private MailgunApiClient mailgunApiClient;

  @Test
  void sendEmail() {
    // Given
    MailgunRequest request =
        MailgunRequest.builder()
            .to("jonghwa@pincoin.co.kr")
            .subject("Mailgun 이메일 발송 테스트")
            .text("이것은 Mailgun API 테스트 메일입니다.")
            .build();

    // When
    MailgunResponse response = mailgunApiClient.sendEmail(request).block();

    // Then
    assertNotNull(response);
    assertEquals("Queued. Thank you.", response.getMessage());
    assertNotNull(response.getId());
  }
}
