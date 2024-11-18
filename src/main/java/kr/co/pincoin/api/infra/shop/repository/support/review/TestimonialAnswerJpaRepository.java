package kr.co.pincoin.api.infra.shop.repository.support.review;

import kr.co.pincoin.api.domain.shop.model.support.review.TestimonialAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestimonialAnswerJpaRepository extends JpaRepository<TestimonialAnswer, Long> {
}