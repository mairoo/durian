package kr.co.pincoin.api.infra.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.pincoin.api.infra.common.BaseDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shop_emailtemplate")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class EmailTemplateEntity extends BaseDateTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "template_name")
  private String templateName;

  @Column(name = "html_content", columnDefinition = "TEXT")
  private String htmlContent;

  @Column(name = "text_content", columnDefinition = "TEXT")
  private String textContent;

  @Column(name = "subject")
  private String subject;
}
