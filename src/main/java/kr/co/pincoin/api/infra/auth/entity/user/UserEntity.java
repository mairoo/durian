package kr.co.pincoin.api.infra.auth.entity.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "auth_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 프록시 생성 목적
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Builder 패턴 사용 목적
@Builder // 객체 생성의 편의성
@Getter
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "password")
  private String password;

  @Column(name = "last_login")
  private LocalDateTime lastLogin;

  @Column(name = "is_superuser")
  private Boolean isSuperuser;

  @Column(name = "username")
  private String username;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email")
  private String email;

  @Column(name = "is_staff")
  private Boolean isStaff;

  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "date_joined")
  private LocalDateTime dateJoined;
}
