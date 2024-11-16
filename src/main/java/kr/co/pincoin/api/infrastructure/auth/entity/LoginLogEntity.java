package kr.co.pincoin.api.infrastructure.auth.entity;

import jakarta.persistence.*;
import kr.co.pincoin.api.domain.common.BaseDateTime;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "member_loginlog")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Slf4j
public class LoginLogEntity extends BaseDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ip_address")
    private String ipAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
