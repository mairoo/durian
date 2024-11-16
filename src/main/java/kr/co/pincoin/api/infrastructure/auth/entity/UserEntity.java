package kr.co.pincoin.api.infrastructure.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Slf4j
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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
