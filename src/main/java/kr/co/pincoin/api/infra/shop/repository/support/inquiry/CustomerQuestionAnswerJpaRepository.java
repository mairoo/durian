package kr.co.pincoin.api.infra.shop.repository.support.inquiry;

import kr.co.pincoin.api.domain.shop.model.support.inquiry.CustomerQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerQuestionAnswerJpaRepository extends JpaRepository<CustomerQuestionAnswer, Long> {
}