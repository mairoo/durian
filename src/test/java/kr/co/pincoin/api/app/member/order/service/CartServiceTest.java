package kr.co.pincoin.api.app.member.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.domain.shop.model.order.Cart;
import kr.co.pincoin.api.domain.shop.repository.order.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // User 도메인 모델을 이용해 테스트 유저 생성
        testUser = User.of(
            "test@example.com",
            "password123",
            "testuser",
            "Test",
            "User"
        );

        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("동시에 여러 스레드가 장바구니를 업데이트할 때 낙관적 락이 동작하는지 확인")
    void testOptimisticLockWithConcurrentUpdates() throws InterruptedException {
        // Given
        Cart initialCart = Cart.createEmptyCart(testUser);
        initialCart = cartRepository.save(initialCart);

        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger failureCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadNum = i;
            executorService.submit(() -> {
                try {
                    cartService.syncCartData(testUser, "{\"items\": [" + threadNum + "]}");
                } catch (ObjectOptimisticLockingFailureException e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Then
        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        assertThat(failureCount.get()).isGreaterThan(0);
        assertThat(cartRepository.findByUser(testUser)).isPresent();
    }

    @Test
    @DisplayName("버전이 다른 장바구니 업데이트 시 예외 발생 확인")
        // @Transactional - 모든 업데이트가 동일한 트랜잭션 내에서 실행되므로 주석처리
    void testOptimisticLockWithStaleVersion() {
        // Given
        Cart initialCart = Cart.createEmptyCart(testUser);
        initialCart = cartRepository.save(initialCart);

        // When & Then
        cartService.syncCartData(testUser, "{\"items\": [1]}");

        // 다른 트랜잭션에서 동일한 카트를 수정하려고 시도
        assertThrows(ObjectOptimisticLockingFailureException.class, () ->
            cartService.syncCartData(testUser, "{\"items\": [2]}")
        );
    }

    @Test
    @DisplayName("순차적인 업데이트는 성공하는지 확인")
        // @Transactional - 모든 업데이트가 동일한 트랜잭션 내에서 실행되므로 주석처리
    void testSequentialUpdates() {
        // Given
        Cart initialCart = Cart.createEmptyCart(testUser);
        initialCart = cartRepository.save(initialCart);

        // When
        Cart firstUpdate = cartService.syncCartData(testUser, "{\"items\": [1]}");
        Cart secondUpdate = cartService.syncCartData(testUser, "{\"items\": [1, 2]}");

        // Then
        assertThat(secondUpdate.getVersion()).isGreaterThan(firstUpdate.getVersion());
        assertThat(secondUpdate.getCartData()).contains("1, 2");
    }
}