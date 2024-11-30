package kr.co.pincoin.api.infra.notification.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.notifcation.model.EmailTemplate;
import kr.co.pincoin.api.infra.notification.entity.EmailTemplateEntity;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateMapper {

  public EmailTemplate toModel(EmailTemplateEntity entity) {
    if (entity == null) {
      return null;
    }

    return EmailTemplate.builder()
        .id(entity.getId())
        .templateName(entity.getTemplateName())
        .htmlContent(entity.getHtmlContent())
        .textContent(entity.getTextContent())
        .subject(entity.getSubject())
        .created(entity.getCreated())
        .modified(entity.getModified())
        .build();
  }

  public EmailTemplateEntity toEntity(EmailTemplate model) {
    if (model == null) {
      return null;
    }

    return model.toEntity();
  }

  public List<EmailTemplate> toModelList(List<EmailTemplateEntity> entities) {
    if (entities == null) {
      return Collections.emptyList();
    }

    return entities.stream().map(this::toModel).collect(Collectors.toList());
  }

  public List<EmailTemplateEntity> toEntityList(List<EmailTemplate> models) {
    if (models == null) {
      return Collections.emptyList();
    }

    return models.stream().map(this::toEntity).collect(Collectors.toList());
  }
}
