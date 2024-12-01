package kr.co.pincoin.api.infra.notification.repository;

import java.util.Optional;
import kr.co.pincoin.api.domain.notifcation.model.EmailTemplate;
import kr.co.pincoin.api.domain.notifcation.repository.EmailTemplateRepository;
import kr.co.pincoin.api.infra.notification.mapper.EmailTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailTemplateRepositoryImpl implements EmailTemplateRepository {

  private final EmailTemplateJpaRepository jpaRepository;

  private final EmailTemplateMapper mapper;

  @Override
  public EmailTemplate save(EmailTemplate emailTemplate) {
    return mapper.toModel(jpaRepository.save(emailTemplate.toEntity()));
  }

  @Override
  public void delete(EmailTemplate emailTemplate) {
    jpaRepository.delete(emailTemplate.toEntity());
  }

  @Override
  public Optional<EmailTemplate> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toModel);
  }

  @Override
  public Optional<EmailTemplate> findByTemplateName(String templateName) {
    return jpaRepository.findByTemplateName(templateName).map(mapper::toModel);
  }
}
