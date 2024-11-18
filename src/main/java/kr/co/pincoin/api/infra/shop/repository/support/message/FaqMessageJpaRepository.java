package kr.co.pincoin.api.infra.shop.repository.support.message;

import kr.co.pincoin.api.infra.shop.entity.support.message.FaqMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqMessageJpaRepository extends JpaRepository<FaqMessageEntity, Long> {
}