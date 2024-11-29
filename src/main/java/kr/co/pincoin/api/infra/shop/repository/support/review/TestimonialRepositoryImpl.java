package kr.co.pincoin.api.infra.shop.repository.support.review;

import kr.co.pincoin.api.domain.shop.repository.support.review.TestimonialRepository;
import kr.co.pincoin.api.infra.shop.mapper.support.review.TestimonialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TestimonialRepositoryImpl implements TestimonialRepository {
  private final TestimonialJpaRepository testimonialJpaRepository;

  private final TestimonialQueryRepository testimonialQueryRepository;

  private final TestimonialMapper testimonialMapper;
}
