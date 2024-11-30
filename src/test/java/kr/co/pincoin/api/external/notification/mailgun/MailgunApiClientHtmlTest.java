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
class MailgunApiClientHtmlTest {

  @Autowired private MailgunApiClient mailgunApiClient;

  @Test
  void sendHtmlEmail() {
    // Given
    String htmlContent =
        """
                <html>
                    <head>
                        <style>
                            .container {
                                font-family: Arial, sans-serif;
                                padding: 20px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h1>HTML 테스트 이메일</h1>
                            <p>이것은 <strong>Mailgun API HTML</strong> 테스트 메일입니다.</p>
                        </div>
                    </body>
                </html>
                """;

    MailgunRequest request =
        MailgunRequest.builder()
            .to("jonghwa@pincoin.co.kr")
            .subject("Mailgun HTML 이메일 발송 테스트")
            .text("이것은 Mailgun API HTML 테스트 메일입니다.")
            .html(htmlContent)
            .build();

    // When
    MailgunResponse response = mailgunApiClient.sendEmail(request).block();

    // Then
    assertNotNull(response);
    assertEquals("Queued. Thank you.", response.getMessage());
    assertNotNull(response.getId());
  }
}
