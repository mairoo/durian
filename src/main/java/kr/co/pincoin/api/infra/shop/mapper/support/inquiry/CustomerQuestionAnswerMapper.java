package kr.co.pincoin.api.infra.shop.mapper.support.inquiry;

import kr.co.pincoin.api.domain.shop.model.support.inquiry.CustomerQuestionAnswer;
import kr.co.pincoin.api.infra.shop.entity.support.inquiry.CustomerQuestionAnswerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerQuestionAnswerMapper {
    private final CustomerQuestionMapper customerQuestionMapper;

    public CustomerQuestionAnswer toModel(CustomerQuestionAnswerEntity entity) {
        if (entity == null) {
            return null;
        }

        return CustomerQuestionAnswer.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .question(customerQuestionMapper.toModel(entity.getQuestion()))
                .created(entity.getCreated())
                .modified(entity.getModified())
                .build();
    }

    public CustomerQuestionAnswerEntity toEntity(CustomerQuestionAnswer model) {
        if (model == null) {
            return null;
        }

        return model.toEntity();
    }

    public List<CustomerQuestionAnswer> toModelList(List<CustomerQuestionAnswerEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public List<CustomerQuestionAnswerEntity> toEntityList(List<CustomerQuestionAnswer> models) {
        if (models == null) {
            return Collections.emptyList();
        }

        return models.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}