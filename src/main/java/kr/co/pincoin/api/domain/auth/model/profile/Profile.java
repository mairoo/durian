package kr.co.pincoin.api.domain.auth.model.profile;

import kr.co.pincoin.api.domain.auth.model.phone.enums.Domestic;
import kr.co.pincoin.api.domain.auth.model.phone.enums.Gender;
import kr.co.pincoin.api.domain.auth.model.phone.enums.PhoneVerifiedStatus;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.infra.auth.entity.profile.ProfileEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Getter
public class Profile {
    private final Long id;

    private final User user;

    private final LocalDateTime created;

    private final LocalDateTime modified;

    private String phone;

    private String address;

    private boolean phoneVerified;

    private PhoneVerifiedStatus phoneVerifiedStatus;

    private boolean documentVerified;

    private boolean allowOrder;

    private String photoId;

    private String card;

    private int totalOrderCount;

    private LocalDateTime firstPurchased;

    private LocalDateTime lastPurchased;

    private boolean notPurchasedMonths;

    private LocalDateTime repurchased;

    private BigDecimal maxPrice;

    private BigDecimal totalListPrice;

    private BigDecimal totalSellingPrice;

    private BigDecimal averagePrice;

    private BigDecimal mileage;

    private String memo;

    private LocalDate dateOfBirth;

    private Gender gender;

    private Domestic domestic;

    private String telecom;

    @Builder(access = AccessLevel.PRIVATE)
    private Profile(Long id,
                    String phone,
                    String address,
                    boolean phoneVerified,
                    PhoneVerifiedStatus phoneVerifiedStatus,
                    boolean documentVerified,
                    boolean allowOrder,
                    String photoId,
                    String card,
                    int totalOrderCount,
                    LocalDateTime firstPurchased,
                    LocalDateTime lastPurchased,
                    boolean notPurchasedMonths,
                    LocalDateTime repurchased,
                    BigDecimal maxPrice,
                    BigDecimal totalListPrice,
                    BigDecimal totalSellingPrice,
                    BigDecimal averagePrice,
                    BigDecimal mileage,
                    String memo,
                    LocalDate dateOfBirth,
                    Gender gender,
                    Domestic domestic,
                    String telecom,
                    User user,
                    LocalDateTime created,
                    LocalDateTime modified) {
        this.id = id;
        this.phone = phone;
        this.address = address;
        this.phoneVerified = phoneVerified;
        this.phoneVerifiedStatus = phoneVerifiedStatus;
        this.documentVerified = documentVerified;
        this.allowOrder = allowOrder;
        this.photoId = photoId;
        this.card = card;
        this.totalOrderCount = totalOrderCount;
        this.firstPurchased = firstPurchased;
        this.lastPurchased = lastPurchased;
        this.notPurchasedMonths = notPurchasedMonths;
        this.repurchased = repurchased;
        this.maxPrice = maxPrice;
        this.totalListPrice = totalListPrice;
        this.totalSellingPrice = totalSellingPrice;
        this.averagePrice = averagePrice;
        this.mileage = mileage != null ? mileage : BigDecimal.ZERO;
        this.memo = memo;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.domestic = domestic;
        this.telecom = telecom;
        this.user = user;
        this.created = created;
        this.modified = modified;
    }

    // 새로운 프로필 생성
    public static Profile of(User user) {
        return Profile.builder()
                .phoneVerified(false)
                .phoneVerifiedStatus(PhoneVerifiedStatus.UNVERIFIED)
                .documentVerified(false)
                .allowOrder(false)
                .totalOrderCount(0)
                .totalListPrice(BigDecimal.ZERO)
                .totalSellingPrice(BigDecimal.ZERO)
                .mileage(BigDecimal.ZERO)
                .user(user)
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .build();
    }

    // 엔티티로부터 도메인 모델 생성
    public static Profile from(ProfileEntity entity) {
        return Profile.builder()
                .id(entity.getId())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .phoneVerified(entity.getPhoneVerified())
                .phoneVerifiedStatus(entity.getPhoneVerifiedStatus())
                .documentVerified(entity.getDocumentVerified())
                .allowOrder(entity.getAllowOrder())
                .photoId(entity.getPhotoId())
                .card(entity.getCard())
                .totalOrderCount(entity.getTotalOrderCount())
                .firstPurchased(entity.getFirstPurchased())
                .lastPurchased(entity.getLastPurchased())
                .notPurchasedMonths(entity.getNotPurchasedMonths())
                .repurchased(entity.getRepurchased())
                .maxPrice(entity.getMaxPrice())
                .totalListPrice(entity.getTotalListPrice())
                .totalSellingPrice(entity.getTotalSellingPrice())
                .averagePrice(entity.getAveragePrice())
                .mileage(entity.getMileage())
                .memo(entity.getMemo())
                .dateOfBirth(entity.getDateOfBirth())
                .gender(entity.getGender())
                .domestic(entity.getDomestic())
                .telecom(entity.getTelecom())
                .user(User.from(entity.getUser()))
                .created(entity.getCreated())
                .modified(entity.getModified())
                .build();
    }

    // 도메인 로직 메소드
    public void verifyPhone(String phone) {
        this.phone = phone;
        this.phoneVerified = true;
        this.phoneVerifiedStatus = PhoneVerifiedStatus.VERIFIED;
    }

    public void verifyDocument() {
        this.documentVerified = true;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void uploadPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public void updateCard(String card) {
        this.card = card;
    }

    public void allowOrder() {
        this.allowOrder = true;
    }

    public void disallowOrder() {
        this.allowOrder = false;
    }

    public void updatePersonalInfo(LocalDate dateOfBirth, Gender gender,
                                   Domestic domestic, String telecom) {
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.domestic = domestic;
        this.telecom = telecom;
    }

    public void addMileage(BigDecimal amount) {
        this.mileage = this.mileage.add(amount);
    }

    public void subtractMileage(BigDecimal amount) {
        BigDecimal newMileage = this.mileage.subtract(amount);
        if (newMileage.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Mileage cannot be negative");
        }
        this.mileage = newMileage;
    }

    public void recordPurchase(BigDecimal listPrice, BigDecimal sellingPrice) {
        this.totalOrderCount++;
        this.totalListPrice = this.totalListPrice.add(listPrice);
        this.totalSellingPrice = this.totalSellingPrice.add(sellingPrice);
        this.averagePrice = this.totalSellingPrice.divide(BigDecimal.valueOf(totalOrderCount),
                                                          2,
                                                          BigDecimal.ROUND_HALF_UP);

        if (sellingPrice.compareTo(this.maxPrice != null ? this.maxPrice : BigDecimal.ZERO) > 0) {
            this.maxPrice = sellingPrice;
        }

        if (this.firstPurchased == null) {
            this.firstPurchased = LocalDateTime.now();
        }
        this.lastPurchased = LocalDateTime.now();

        if (this.notPurchasedMonths) {
            this.repurchased = LocalDateTime.now();
        }
        this.notPurchasedMonths = false;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    // 비즈니스 로직
    public boolean canOrder() {
        return this.allowOrder && this.phoneVerified && this.documentVerified;
    }

    public int getAge() {
        if (this.dateOfBirth == null) {
            return 0;
        }
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    public boolean isRegularCustomer() {
        return this.totalOrderCount >= 5;
    }

    public BigDecimal getTotalDiscount() {
        return this.totalListPrice.subtract(this.totalSellingPrice);
    }

    public double getAverageDiscountRate() {
        if (this.totalListPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return this.getTotalDiscount().divide(this.totalListPrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}