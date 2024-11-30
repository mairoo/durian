package kr.co.pincoin.api.domain.notifcation.model;

import java.time.LocalDateTime;
import java.util.Map;
import kr.co.pincoin.api.infra.notification.entity.EmailTemplateEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailTemplate {

  private final Long id;

  private final String templateName;

  private final String htmlContent;

  private final String textContent;

  private final String subject;

  private final LocalDateTime created;

  private final LocalDateTime modified;

  @Builder
  private EmailTemplate(
      Long id,
      String templateName,
      String htmlContent,
      String textContent,
      String subject,
      LocalDateTime created,
      LocalDateTime modified) {
    this.id = id;
    this.templateName = templateName;
    this.htmlContent = htmlContent;
    this.textContent = textContent;
    this.subject = subject;
    this.created = created;
    this.modified = modified;
  }

  public static EmailTemplate of(
      String templateName, String htmlContent, String textContent, String subject) {
    return EmailTemplate.builder()
        .templateName(templateName)
        .htmlContent(htmlContent)
        .textContent(textContent)
        .subject(subject)
        .build();
  }

  public EmailTemplateEntity toEntity() {
    return EmailTemplateEntity.builder()
        .id(this.getId())
        .templateName(this.getTemplateName())
        .htmlContent(this.getHtmlContent())
        .textContent(this.getTextContent())
        .subject(this.getSubject())
        .build();
  }

  // 1. 변수 치환 메소드
  public EmailTemplate replaceVariables(java.util.Map<String, String> variables) {
    String newHtmlContent = this.htmlContent;
    String newTextContent = this.textContent;
    String newSubject = this.subject;

    for (Map.Entry<String, String> entry : variables.entrySet()) {
      String placeholder = "${" + entry.getKey() + "}";
      newHtmlContent = newHtmlContent.replace(placeholder, entry.getValue());
      newTextContent = newTextContent.replace(placeholder, entry.getValue());
      newSubject = newSubject.replace(placeholder, entry.getValue());
    }

    return EmailTemplate.builder()
        .id(this.id)
        .templateName(this.templateName)
        .htmlContent(newHtmlContent)
        .textContent(newTextContent)
        .subject(newSubject)
        .created(this.created)
        .modified(this.modified)
        .build();
  }

  // 2. 상태 확인 메소드
  public boolean hasVariable(String variableName) {
    String placeholder = "${" + variableName + "}";
    return this.htmlContent.contains(placeholder)
        || this.textContent.contains(placeholder)
        || this.subject.contains(placeholder);
  }

  public boolean containsHtmlContent() {
    return this.htmlContent != null && !this.htmlContent.isEmpty();
  }

  // 3. 검증 메소드
  public boolean isValid() {
    return this.templateName != null && this.subject != null && this.textContent != null;
  }
}
