# 최상단 패키지 구조

- domain: 순수한 비즈니스 로직
- infrastructure:
    - persistence: 데이터 접근 관련
- application: 응용프로그램
    - controller: 컨트롤러
- global: 전역 설정
    - config
        - JpaConfig
        - WebConfig
        - SecurityConfig
        - RedisConfig
    - response
    - security
- external
    - aligo
    - billgate
    - mailgun
    - paypal
    - s3

# 역할

도메인 모델

- **주로 단일 객체에 대한 책임 캡슐화**
- 자신의 상태 관리 (값 변경, 불변성 보장)
- 자신의 컬렉션 관리 (추가/삭제)
- 기본적인 비즈니스 규칙 검증 (validate)

도메인 서비스

- **여러 도메인 객체 간의 상호작용**
- 복잡한 비즈니스 로직 구현
- 트랜잭션 일관성 보장
- 실제 "비즈니스 프로세스" 구현

리파지토리

- 도메인 계층
    - OrderRepository
        - 순수 자바 인터페이스
        - JPA + QueryDSL 쿼리 모두 호출 가능

- 인프라 계층
    - OrderJpaRepository
        - @Repository
        - JpaRepository 상속(extends)
    - OrderQueryRepository
        - 순수 자바 인터페이스
        - QueryDSL 용도
    - OrderQueryRepositoryImpl
        - @Repository
        - OrderQueryRepository 구현체(implements)
    - OrderRepositoryImpl
        - @Repository
        - OrderRepository 구현체
        - OrderJpaRepository + OrderQueryRepository + OrderMapper (Composition 패턴 / has-a)

검색조건과 빌더 패턴

- 불변성 보장
- 잘못된 검색 조건 조합 방지 유효성 검증
