package kr.co.pincoin.api.infra.shop.repository.support.inquiry;

import kr.co.pincoin.api.domain.shop.repository.support.inquiry.CustomerQuestionRepository;
import kr.co.pincoin.api.infra.shop.mapper.support.inquiry.CustomerQuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomerQuestionRepositoryImpl implements CustomerQuestionRepository {

  private final CustomerQuestionJpaRepository jpaRepository;

  private final CustomerQuestionQueryRepository queryRepository;

  private final CustomerQuestionMapper mapper;
}
