package kr.co.pincoin.api.domain.notifcation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import kr.co.pincoin.api.domain.notifcation.model.EmailTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmailTemplateServiceTest {

    @Autowired
    private EmailTemplateService emailTemplateService;

    @Test
    @DisplayName("이메일 템플릿 생성 및 변수 치환 테스트")
    void createAndReplaceTemplateVariables() {
        // Given
        String templateName = "test-welcome";
        String htmlContent = """
            <html>
                <body>
                    <h1>${company} 가입을 환영합니다!</h1>
                    <p>안녕하세요 ${userName}님,</p>
                    <p>가입해 주셔서 감사합니다.</p>
                </body>
            </html>
            """;
        String textContent = """
            ${company} 가입을 환영합니다!
            안녕하세요 ${userName}님,
            가입해 주셔서 감사합니다.
            """;
        String subject = "${company} 회원 가입을 환영합니다";

        EmailTemplate template = EmailTemplate.of(
            templateName,
            htmlContent,
            textContent,
            subject
        );

        // When
        EmailTemplate savedTemplate = emailTemplateService.createTemplate(template);

        // Then
        assertNotNull(savedTemplate);
        assertEquals(templateName, savedTemplate.getTemplateName());
        assertTrue(savedTemplate.hasVariable("company"));
        assertTrue(savedTemplate.hasVariable("userName"));

        // Given - 변수 치환 테스트
        Map<String, String> variables = new HashMap<>();
        variables.put("company", "핀코인");
        variables.put("userName", "홍길동");

        // When - 변수 치환 실행
        EmailTemplate replacedTemplate = emailTemplateService.getTemplateWithVariables(
            savedTemplate.getTemplateName(),
            variables
        );

        // Then - 변수 치환 결과 확인
        assertNotNull(replacedTemplate);
        assertTrue(replacedTemplate.getHtmlContent().contains("핀코인 가입을 환영합니다"));
        assertTrue(replacedTemplate.getHtmlContent().contains("홍길동님"));
        assertTrue(replacedTemplate.getTextContent().contains("핀코인 가입을 환영합니다"));
        assertTrue(replacedTemplate.getTextContent().contains("홍길동님"));
        assertEquals("핀코인 회원 가입을 환영합니다", replacedTemplate.getSubject());
    }

    @Test
    @DisplayName("이메일 템플릿 조회 및 삭제 테스트")
    void findAndDeleteTemplate() {
        // Given
        EmailTemplate template = EmailTemplate.of(
            "test-notification",
            "<p>테스트 HTML</p>",
            "테스트 텍스트",
            "테스트 제목"
        );
        EmailTemplate savedTemplate = emailTemplateService.createTemplate(template);

        // When - 템플릿 조회
        EmailTemplate foundTemplate = emailTemplateService.getTemplate(
            savedTemplate.getTemplateName());

        // Then - 조회 결과 확인
        assertNotNull(foundTemplate);
        assertEquals("test-notification", foundTemplate.getTemplateName());

        // When - 템플릿 삭제
        emailTemplateService.deleteTemplate(savedTemplate.getTemplateName());

        // Then - 삭제 확인
        assertThrows(RuntimeException.class, () ->
            emailTemplateService.getTemplate(savedTemplate.getTemplateName())
        );
    }

    @Test
    @DisplayName("이메일 템플릿 이름 중복 체크")
    void duplicateTemplateNameCheck() {
        // Given
        String templateName = "test-duplicate";
        EmailTemplate template1 = EmailTemplate.of(
            templateName,
            "<p>테스트1</p>",
            "테스트1",
            "제목1"
        );

        // When
        emailTemplateService.createTemplate(template1);

        // Then
        EmailTemplate template2 = EmailTemplate.of(
            templateName,
            "<p>테스트2</p>",
            "테스트2",
            "제목2"
        );

        assertThrows(RuntimeException.class, () ->
            emailTemplateService.createTemplate(template2)
        );
    }
}