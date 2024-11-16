package kr.co.pincoin.api.infrastructure.auth.entity;

import jakarta.persistence.*;
import kr.co.pincoin.api.infrastructure.common.BaseDateTime;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "member_phoneverificationlog")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Slf4j
public class PhoneVerificationLogEntity extends BaseDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "code")
    private String code;

    @Column(name = "reason")
    private String reason;

    @Column(name = "result_code")
    private String resultCode;

    @Column(name = "message")
    private String message;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "di")
    private String di;

    @Column(name = "ci")
    private String ci;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "domestic")
    private Integer domestic;

    @Column(name = "telecom")
    private String telecom;

    @Column(name = "cellphone")
    private String cellphone;

    @Column(name = "return_message")
    private String returnMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;
}