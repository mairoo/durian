package kr.co.pincoin.api.infra.shop.repository.support.review;

import kr.co.pincoin.api.domain.shop.repository.support.review.TestimonialAnswerRepository;
import kr.co.pincoin.api.infra.shop.mapper.support.review.TestimonialAnswerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TestimonialAnswerRepositoryImpl implements TestimonialAnswerRepository {
  private final TestimonialAnswerJpaRepository testimonialAnswerJpaRepository;

  private final TestimonialAnswerQueryRepository testimonialAnswerQueryRepository;

  private final TestimonialAnswerMapper testimonialAnswerMapper;
}
