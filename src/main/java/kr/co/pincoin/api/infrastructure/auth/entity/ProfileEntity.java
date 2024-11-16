package kr.co.pincoin.api.infrastructure.auth.entity;

import jakarta.persistence.*;
import kr.co.pincoin.api.infrastructure.common.BaseDateTime;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "member_profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Slf4j
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

    @Column(name = "phone_verified_status")
    private Integer phoneVerifiedStatus;

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

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "domestic")
    private Integer domestic;

    @Column(name = "telecom")
    private String telecom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}