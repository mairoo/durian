package kr.co.pincoin.api.infra.shop.repository.support.message;

import kr.co.pincoin.api.domain.shop.repository.support.message.FaqMessageRepository;
import kr.co.pincoin.api.infra.shop.mapper.support.message.FaqMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FaqMessageRepositoryImpl implements FaqMessageRepository {
    private final FaqMessageJpaRepository faqMessageJpaRepository;

    private final FaqMessageQueryRepository faqMessageQueryRepository;

    private final FaqMessageMapper faqMessageMapper;
}