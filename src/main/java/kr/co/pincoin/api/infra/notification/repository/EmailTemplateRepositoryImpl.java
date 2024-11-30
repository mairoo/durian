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

    private final EmailTemplateMapper emailTemplateMapper;

    private final EmailTemplateJpaRepository emailTemplateJpaRepository;

    @Override
    public EmailTemplate save(EmailTemplate emailTemplate) {
        return emailTemplateMapper.toModel(
            emailTemplateJpaRepository.save(emailTemplate.toEntity()));
    }

    @Override
    public void delete(EmailTemplate emailTemplate) {
        emailTemplateJpaRepository.delete(emailTemplate.toEntity());
    }

    @Override
    public Optional<EmailTemplate> findById(Long id) {
        return emailTemplateJpaRepository.findById(id).map(emailTemplateMapper::toModel);
    }

    @Override
    public Optional<EmailTemplate> findByTemplateName(String templateName) {
        return emailTemplateJpaRepository.findByTemplateName(templateName)
            .map(emailTemplateMapper::toModel);
    }

    @Override
    public boolean existsByTemplateName(String templateName) {
        return emailTemplateJpaRepository.existsByTemplateName(templateName);
    }
}
