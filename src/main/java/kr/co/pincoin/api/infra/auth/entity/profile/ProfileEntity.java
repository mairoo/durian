package kr.co.pincoin.api.infra.auth.entity.profile;

import jakarta.persistence.*;
import kr.co.pincoin.api.domain.auth.model.phone.enums.Domestic;
import kr.co.pincoin.api.domain.auth.model.phone.enums.Gender;
import kr.co.pincoin.api.domain.auth.model.phone.enums.PhoneVerifiedStatus;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import kr.co.pincoin.api.infra.common.BaseDateTime;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ProfileEntity extends BaseDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_verified")
    private Boolean phoneVerified;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "phone_verified_status", columnDefinition = "INT")
    private PhoneVerifiedStatus phoneVerifiedStatus;

    @Column(name = "document_verified")
    private Boolean documentVerified;

    @Column(name = "allow_order")
    private Boolean allowOrder;

    @Column(name = "photo_id")
    private String photoId;

    @Column(name = "card")
    private String card;

    @Column(name = "total_order_count")
    private Integer totalOrderCount;

    @Column(name = "first_purchased")
    private LocalDateTime firstPurchased;

    @Column(name = "last_purchased")
    private LocalDateTime lastPurchased;

    @Column(name = "not_purchased_months")
    private Boolean notPurchasedMonths;

    @Column(name = "repurchased")
    private LocalDateTime repurchased;

    @Column(name = "max_price")
    private BigDecimal maxPrice;

    @Column(name = "total_list_price")
    private BigDecimal totalListPrice;

    @Column(name = "total_selling_price")
    private BigDecimal totalSellingPrice;

    @Column(name = "average_price")
    private BigDecimal averagePrice;

    @Column(name = "mileage")
    private BigDecimal mileage;

    @Column(name = "memo")
    private String memo;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "gender", columnDefinition = "INT")
    private Gender gender;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "domestic", columnDefinition = "INT")
    private Domestic domestic;

    @Column(name = "telecom")
    private String telecom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}