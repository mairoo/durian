package kr.co.pincoin.api.infra.auth.repository.email;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.co.pincoin.api.domain.auth.model.email.BannedEmail;
import kr.co.pincoin.api.domain.auth.repository.email.BannedEmailRepository;
import kr.co.pincoin.api.infra.auth.entity.email.BannedEmailEntity;
import kr.co.pincoin.api.infra.auth.mapper.email.BannedEmailMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({BannedEmailRepositoryImpl.class, BannedEmailMapper.class, BannedEmailQueryRepositoryImpl.class, BannedEmailRepositoryTest.TestConfig.class})
@ActiveProfiles("test")
class BannedEmailRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BannedEmailRepository bannedEmailRepository;

    @Autowired
    private BannedEmailMapper bannedEmailMapper;

    private BannedEmailEntity testBannedEmailEntity;

    @BeforeEach
    void setUp() {
        testBannedEmailEntity = BannedEmailEntity.builder()
                .email("test@example.com")
                .build();

        entityManager.persistAndFlush(testBannedEmailEntity);
        entityManager.clear();
    }

    @Test
    @DisplayName("이메일 차단 저장 테스트")
    void saveBannedEmail() {
        // when
        BannedEmail bannedEmail = bannedEmailMapper.toModel(testBannedEmailEntity);
        BannedEmail savedEmail = bannedEmailRepository.save(bannedEmail);

        // then
        assertNotNull(savedEmail.getId());
        assertEquals(testBannedEmailEntity.getEmail(), savedEmail.getEmail());
        assertFalse(savedEmail.isRemoved());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
            return new JPAQueryFactory(entityManager);
        }
    }
}