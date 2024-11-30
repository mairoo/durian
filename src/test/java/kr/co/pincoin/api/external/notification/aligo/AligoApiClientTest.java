package kr.co.pincoin.api.external.notification.aligo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pincoin.api.global.config.WebClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = {WebClientConfig.class, AligoApiClient.class, ObjectMapper.class}
)
@ActiveProfiles("test")
class AligoApiClientTest {

    @Autowired
    private AligoApiClient aligoApiClient;

    @Test
    void sendSms() {
        // Given
        AligoSmsRequest request = AligoSmsRequest.builder()
            .receiver("01012341234")  // 실제 수신할 전화번호
            .message("알리고 문자발송 테스트")
            .build();

        // When
        AligoSmsResponse response = aligoApiClient.sendSms(request).block();

        assertNotNull(response);
        assertEquals("1", response.getResultCode());
        assertEquals("success", response.getMessage());
    }
}