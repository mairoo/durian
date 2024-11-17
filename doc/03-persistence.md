# JpaConfig

# QueryDSL

QueryDslConfig
```
@Configuration
@RequiredArgsConstructor
@Slf4j
public class QueryDslConfig {
    @PersistenceContext
    private final EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }
}
```