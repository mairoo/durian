package kr.co.pincoin.api.infra.shop.repository.support.inquiry;

import kr.co.pincoin.api.domain.shop.repository.support.inquiry.CustomerQuestionAnswerRepository;
import kr.co.pincoin.api.infra.shop.mapper.support.inquiry.CustomerQuestionAnswerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomerQuestionAnswerRepositoryImpl implements CustomerQuestionAnswerRepository {

  private final CustomerQuestionAnswerJpaRepository jpaRepository;

  private final CustomerQuestionAnswerQueryRepository queryRepository;

  private final CustomerQuestionAnswerMapper mapper;
}
