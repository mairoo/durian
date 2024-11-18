package kr.co.pincoin.api.infra.shop.repository.support.message;

import kr.co.pincoin.api.domain.shop.model.support.message.Sms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsJpaRepository extends JpaRepository<Sms, Long> {
}