package kr.co.pincoin.api.infra.auth.entity.phone;

import jakarta.persistence.*;
import kr.co.pincoin.api.domain.auth.model.phone.enums.Domestic;
import kr.co.pincoin.api.domain.auth.model.phone.enums.Gender;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import kr.co.pincoin.api.infra.common.BaseDateTime;
import lombok.*;

@Entity
@Table(name = "member_phoneverificationlog")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
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

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "gender", columnDefinition = "INT")
    private Gender gender;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "domestic", columnDefinition = "INT")
    private Domestic domestic;

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