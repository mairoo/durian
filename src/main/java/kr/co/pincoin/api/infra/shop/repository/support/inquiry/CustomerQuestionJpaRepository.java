package kr.co.pincoin.api.infra.shop.repository.support.inquiry;

import kr.co.pincoin.api.infra.shop.entity.support.inquiry.CustomerQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerQuestionJpaRepository
    extends JpaRepository<CustomerQuestionEntity, Long> {}
