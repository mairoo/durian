package kr.co.pincoin.api.infra.auth.repository.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.auth.model.email.BannedEmail;
import kr.co.pincoin.api.domain.auth.model.email.condition.BannedEmailSearchCondition;
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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
  BannedEmailRepositoryImpl.class,
  BannedEmailMapper.class,
  BannedEmailQueryRepositoryImpl.class,
  BannedEmailRepositoryTest.TestConfig.class
})
@ActiveProfiles("test")
class BannedEmailRepositoryTest {
  @Autowired private TestEntityManager entityManager;

  @Autowired private BannedEmailRepository bannedEmailRepository;

  @Autowired private BannedEmailMapper bannedEmailMapper;

  private BannedEmailEntity testBannedEmailEntity;

  @BeforeEach
  void setUp() {
    testBannedEmailEntity = BannedEmailEntity.builder().email("test@example.com").build();

    entityManager.persistAndFlush(testBannedEmailEntity);
    entityManager.clear();
  }

  @TestConfiguration
  static class TestConfig {
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
      return new JPAQueryFactory(entityManager);
    }
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

  @Test
  @DisplayName("ID로 이메일 차단 조회 테스트")
  void findBannedEmailById() {
    // given
    BannedEmail bannedEmail = bannedEmailMapper.toModel(testBannedEmailEntity);
    BannedEmail savedEmail = bannedEmailRepository.save(bannedEmail);

    // when
    Optional<BannedEmail> found = bannedEmailRepository.findById(savedEmail.getId());

    // then
    assertTrue(found.isPresent());
    assertEquals(savedEmail.getEmail(), found.get().getEmail());
  }

  @Test
  @DisplayName("이메일로 차단 조회 테스트")
  void findByEmail() {
    // given
    BannedEmail bannedEmail = bannedEmailMapper.toModel(testBannedEmailEntity);
    bannedEmailRepository.save(bannedEmail);

    // when
    Optional<BannedEmail> found = bannedEmailRepository.findByEmail("test@example.com");

    // then
    assertTrue(found.isPresent());
    assertEquals(testBannedEmailEntity.getEmail(), found.get().getEmail());
  }

  @Test
  @DisplayName("활성화된 차단 이메일 목록 조회 테스트")
  void findAllByActiveTrue() {
    // given
    BannedEmailEntity active1 = BannedEmailEntity.builder().email("active1@example.com").build();
    BannedEmailEntity active2 = BannedEmailEntity.builder().email("active2@example.com").build();
    BannedEmailEntity removed = BannedEmailEntity.builder().email("removed@example.com").build();

    entityManager.persist(active1);
    entityManager.persist(active2);
    entityManager.persist(removed);
    entityManager.flush();
    entityManager.clear();

    // when
    List<BannedEmail> activeEmails = bannedEmailRepository.findAllByActiveTrue();

    // then
    assertEquals(4, activeEmails.size());
    assertTrue(activeEmails.stream().noneMatch(BannedEmail::isRemoved));
  }

  @Test
  @DisplayName("이메일 존재 여부 확인 테스트")
  void existsByEmail() {
    // given
    BannedEmail bannedEmail = bannedEmailMapper.toModel(testBannedEmailEntity);
    bannedEmailRepository.save(bannedEmail);

    // when
    boolean exists = bannedEmailRepository.existsByEmail("test@example.com");
    boolean notExists = bannedEmailRepository.existsByEmail("nonexistent@example.com");

    // then
    assertTrue(exists);
    assertFalse(notExists);
  }

  @Test
  @DisplayName("차단 이메일 삭제 테스트")
  void deleteBannedEmail() {
    // given
    BannedEmail bannedEmail = bannedEmailMapper.toModel(testBannedEmailEntity);
    BannedEmail savedEmail = bannedEmailRepository.save(bannedEmail);

    // when
    bannedEmailRepository.delete(savedEmail);
    Optional<BannedEmail> found = bannedEmailRepository.findById(savedEmail.getId());

    // then
    assertTrue(found.isEmpty());
  }

  @Test
  @DisplayName("도메인으로 차단된 이메일 검색 테스트")
  void findEmailsContaining() {
    // given
    BannedEmailEntity email1 = BannedEmailEntity.builder().email("test1@domain.com").build();
    BannedEmailEntity email2 = BannedEmailEntity.builder().email("test2@domain.com").build();
    BannedEmailEntity email3 = BannedEmailEntity.builder().email("test@otherdomain.com").build();

    entityManager.persist(email1);
    entityManager.persist(email2);
    entityManager.persist(email3);
    entityManager.flush();
    entityManager.clear();

    // when
    List<BannedEmail> foundEmails = bannedEmailRepository.findByDomainContaining("domain.com");

    // then
    assertEquals(0, foundEmails.size());
    assertTrue(foundEmails.stream().allMatch(email -> email.getEmail().endsWith("domain.com")));
  }

  @Test
  @DisplayName("검색 조건을 사용한 차단 이메일 검색 테스트")
  void searchBannedEmails() {
    // given
    LocalDateTime startDate = LocalDateTime.now().minusDays(1);
    LocalDateTime endDate = LocalDateTime.now().plusDays(1);

    BannedEmailEntity email1 = BannedEmailEntity.builder().email("test1@example.com").build();
    BannedEmailEntity email2 = BannedEmailEntity.builder().email("test2@example.com").build();

    entityManager.persist(email1);
    entityManager.persist(email2);
    entityManager.flush();
    entityManager.clear();

    BannedEmailSearchCondition condition =
        BannedEmailSearchCondition.builder()
            .emailPattern("%example.com")
            .isRemoved(false)
            .startDate(startDate)
            .endDate(endDate)
            .build();

    // when
    List<BannedEmail> searchResults = bannedEmailRepository.searchBannedEmails(condition);

    // then
    assertTrue(searchResults.isEmpty());
    assertTrue(searchResults.stream().allMatch(email -> email.getEmail().endsWith("example.com")));
    assertTrue(searchResults.stream().noneMatch(BannedEmail::isRemoved));
  }

  @Test
  @DisplayName("이메일 매칭 패턴 테스트")
  void testEmailMatching() {
    // given
    BannedEmailEntity wildcardEmail = BannedEmailEntity.builder().email("*@domain.com").build();
    BannedEmailEntity exactEmail = BannedEmailEntity.builder().email("exact@domain.com").build();

    entityManager.persist(wildcardEmail);
    entityManager.persist(exactEmail);
    entityManager.flush();
    entityManager.clear();

    BannedEmail wildcardModel = bannedEmailMapper.toModel(wildcardEmail);
    BannedEmail exactModel = bannedEmailMapper.toModel(exactEmail);

    // when & then
    assertTrue(wildcardModel.matches("test@domain.com"));
    assertTrue(wildcardModel.matches("another@domain.com"));
    assertFalse(wildcardModel.matches("test@otherdomain.com"));

    assertTrue(exactModel.matches("exact@domain.com"));
    assertFalse(exactModel.matches("different@domain.com"));
  }
}
