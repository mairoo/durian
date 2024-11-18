package kr.co.pincoin.api.infra.shop.repository.support.review;

import kr.co.pincoin.api.infra.shop.entity.support.review.TestimonialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestimonialJpaRepository extends JpaRepository<TestimonialEntity, Long> {
}