package kr.co.pincoin.api.infra.shop.repository.support.message;

import kr.co.pincoin.api.domain.shop.repository.support.message.NoticeMessageRepository;
import kr.co.pincoin.api.infra.shop.mapper.support.message.NoticeMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeMessageRepositoryImpl implements NoticeMessageRepository {
    private final NoticeMessageJpaRepository noticeMessageJpaRepository;

    private final NoticeMessageQueryRepository noticeMessageQueryRepository;

    private final NoticeMessageMapper noticeMessageMapper;
}