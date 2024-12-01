package kr.co.pincoin.api.domain.notifcation.service;

import java.util.Map;
import kr.co.pincoin.api.domain.notifcation.model.EmailTemplate;
import kr.co.pincoin.api.domain.notifcation.repository.EmailTemplateRepository;
import kr.co.pincoin.api.external.notification.mailgun.MailgunApiClient;
import kr.co.pincoin.api.external.notification.mailgun.MailgunRequest;
import kr.co.pincoin.api.external.notification.mailgun.MailgunResponse;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional(readOnly = true)
@Slf4j
public class EmailTemplateService {

  private final EmailTemplateRepository emailTemplateRepository;

  private final MailgunApiClient mailgunApiClient;

  public EmailTemplateService(
      EmailTemplateRepository emailTemplateRepository, MailgunApiClient mailgunApiClient) {
    this.emailTemplateRepository = emailTemplateRepository;
    this.mailgunApiClient = mailgunApiClient;
  }

  public Mono<MailgunResponse> sendTemplateEmail(
      String templateName, String to, Map<String, String> variables) {
    EmailTemplate template =
        emailTemplateRepository
            .findByTemplateName(templateName)
            .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_TEMPLATE_NOT_FOUND));

    String htmlContent = replaceVariables(template.getHtmlContent(), variables);
    String textContent = replaceVariables(template.getTextContent(), variables);
    String subject = replaceVariables(template.getSubject(), variables);

    MailgunRequest request =
        MailgunRequest.builder()
            .to(to)
            .subject(subject)
            .text(textContent)
            .html(htmlContent)
            .build();

    return mailgunApiClient.sendEmail(request);
  }

  @Transactional
  public EmailTemplate createTemplate(EmailTemplate template) {
    // 템플릿 이름 중복 체크
    if (emailTemplateRepository.findByTemplateName(template.getTemplateName()).isPresent()) {
      throw new BusinessException(ErrorCode.EMAIL_TEMPLATE_ALREADY_EXISTS);
    }

    return emailTemplateRepository.save(template);
  }

  public EmailTemplate getTemplate(String templateName) {
    return emailTemplateRepository
        .findByTemplateName(templateName)
        .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_TEMPLATE_NOT_FOUND));
  }

  public EmailTemplate getTemplateWithVariables(
      String templateName, Map<String, String> variables) {
    EmailTemplate template = getTemplate(templateName);

    String htmlContent = replaceVariables(template.getHtmlContent(), variables);
    String textContent = replaceVariables(template.getTextContent(), variables);
    String subject = replaceVariables(template.getSubject(), variables);

    return EmailTemplate.builder()
        .id(template.getId())
        .templateName(template.getTemplateName())
        .htmlContent(htmlContent)
        .textContent(textContent)
        .subject(subject)
        .created(template.getCreated())
        .modified(template.getModified())
        .build();
  }

  @Transactional
  public void deleteTemplate(String templateName) {
    EmailTemplate template = getTemplate(templateName);
    emailTemplateRepository.delete(template);
  }

  private String replaceVariables(String content, Map<String, String> variables) {
    String result = content;
    for (Map.Entry<String, String> entry : variables.entrySet()) {
      result = result.replace("${" + entry.getKey() + "}", entry.getValue());
    }
    return result;
  }

  // 템플릿 예시를 위한 초기 데이터 삽입 메소드
  @Transactional
  public void createWelcomeTemplate() {
    String htmlTemplate =
        """
                <html>
                    <head>
                        <style>
                            .container {
                                font-family: Arial, sans-serif;
                                max-width: 600px;
                                margin: 0 auto;
                                padding: 20px;
                            }
                            .header {
                                background-color: #f8f9fa;
                                padding: 20px;
                                text-align: center;
                            }
                            .content {
                                padding: 20px;
                                line-height: 1.6;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h1>${company} 가입을 환영합니다!</h1>
                            </div>
                            <div class="content">
                                <p>안녕하세요 ${userName}님,</p>
                                <p>${company}의 회원이 되신 것을 진심으로 환영합니다.</p>
                                <p>계정 활성화를 위해 아래 링크를 클릭해주세요:</p>
                                <p><a href="${activationLink}">계정 활성화하기</a></p>
                            </div>
                        </div>
                    </body>
                </html>
                """;

    String textTemplate =
        """
                ${company} 가입을 환영합니다!

                안녕하세요 ${userName}님,

                ${company}의 회원이 되신 것을 진심으로 환영합니다.
                계정 활성화를 위해 아래 링크를 방문해주세요:

                ${activationLink}
                """;

    EmailTemplate welcomeTemplate =
        EmailTemplate.builder()
            .templateName("welcome")
            .subject("${company} 가입을 환영합니다")
            .htmlContent(htmlTemplate)
            .textContent(textTemplate)
            .build();

    emailTemplateRepository.save(welcomeTemplate);
  }
}
