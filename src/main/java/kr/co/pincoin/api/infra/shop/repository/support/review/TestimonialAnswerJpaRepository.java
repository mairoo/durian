package kr.co.pincoin.api.infra.shop.repository.support.review;

import kr.co.pincoin.api.infra.shop.entity.support.review.TestimonialAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestimonialAnswerJpaRepository
    extends JpaRepository<TestimonialAnswerEntity, Long> {}
