package kr.co.pincoin.api.domain.notifcation.repository;

import java.util.Optional;
import kr.co.pincoin.api.domain.notifcation.model.EmailTemplate;

public interface EmailTemplateRepository {

    EmailTemplate save(EmailTemplate emailTemplate);

    void delete(EmailTemplate emailTemplate);

    Optional<EmailTemplate> findById(Long id);

    Optional<EmailTemplate> findByTemplateName(String templateName);

    boolean existsByTemplateName(String templateName);
}
