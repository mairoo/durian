# 프로젝트 시작

## start.spring.io/

- Project: Gradle - Groovy
- Language: Java
- Spring Boot: 3.3.5
- Project Metadata
    - Group: kr.co.pincoin
    - Artifact: api
    - Name: api
    - Description: Backend service for pincoin
    - Package name: kr.co.pincoin.api
    - Packaging: Jar
    - Java: 21
- Dependencies
    - Developer tools
        - Spring Boot DevTools
        - Lombok
        - Spring Configuration Processor
    - Web
        - Spring Web
    - Security
        - Spring Security
        - OAuth2 Client
    - SQL
        - Spring Data JPA
        - MariaDB Driver

## 압축 풀기

```
git clone ...
cd durian
unzip -o api.zip "api/*" && mv -f api/{.,}* . 2>/dev/null ; rm -r api
```

## application-local.yaml

```
spring:
  application:
    name: api
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: "jdbc:mariadb://localhost/db"
    username: test
    password: test
    hikari:
      connectionInitSql: "SET NAMES utf8mb4"

  jpa:
    hibernate:
      ddl-auto: validate # validate, create, create-drop., update, none
    # `hibernate.ddl-auto` 설정이 `generate-ddl`보다 우선하므로 설정이 무시됨
    # generate-ddl: true
    show-sql: true
    open-in-view: false # 허용 시 뷰 렌더링 중에도 지연 로딩 쿼리가 실행될 수도 있음
logging:
  level:
    root: INFO
```

## 