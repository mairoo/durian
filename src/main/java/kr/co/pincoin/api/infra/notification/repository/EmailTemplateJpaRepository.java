package kr.co.pincoin.api.infra.notification.repository;

import java.util.Optional;
import kr.co.pincoin.api.infra.notification.entity.EmailTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTemplateJpaRepository extends JpaRepository<EmailTemplateEntity, Long> {

  Optional<EmailTemplateEntity> findByTemplateName(String templateName);
}
