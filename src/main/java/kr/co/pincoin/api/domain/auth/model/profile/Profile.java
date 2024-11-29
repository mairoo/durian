package kr.co.pincoin.api.domain.auth.model.profile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import kr.co.pincoin.api.domain.auth.model.phone.enums.Domestic;
import kr.co.pincoin.api.domain.auth.model.phone.enums.Gender;
import kr.co.pincoin.api.domain.auth.model.phone.enums.PhoneVerifiedStatus;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.infra.auth.entity.profile.ProfileEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Profile {
  // 1. 불변 필드 (final)
  private final Long id;

  private final User user;

  private final LocalDateTime created;

  private final LocalDateTime modified;

  // 2. 기본 정보
  private String phone;

  private String address;

  private boolean phoneVerified;

  private PhoneVerifiedStatus phoneVerifiedStatus;

  private boolean documentVerified;

  private boolean allowOrder;

  private String photoId;

  private String card;

  private String memo;

  // 3. 개인 정보
  private LocalDate dateOfBirth;

  private Gender gender;

  private Domestic domestic;

  private String telecom;

  // 4. 구매 통계
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

  @Builder
  private Profile( // 1. 불변 필드
      Long id,
      User user,
      LocalDateTime created,
      LocalDateTime modified,

      // 2. 기본 정보
      String phone,
      String address,
      boolean phoneVerified,
      PhoneVerifiedStatus phoneVerifiedStatus,
      boolean documentVerified,
      boolean allowOrder,
      String photoId,
      String card,
      String memo,

      // 3. 개인 정보
      LocalDate dateOfBirth,
      Gender gender,
      Domestic domestic,
      String telecom,

      // 4. 구매 통계
      int totalOrderCount,
      LocalDateTime firstPurchased,
      LocalDateTime lastPurchased,
      boolean notPurchasedMonths,
      LocalDateTime repurchased,
      BigDecimal maxPrice,
      BigDecimal totalListPrice,
      BigDecimal totalSellingPrice,
      BigDecimal averagePrice,
      BigDecimal mileage) {
    // 1. 불변 필드
    this.id = id;
    this.user = user;
    this.created = created != null ? created : LocalDateTime.now();
    this.modified = modified != null ? modified : LocalDateTime.now();

    // 2. 기본 정보
    this.phone = phone;
    this.address = address;
    this.phoneVerified = phoneVerified;
    this.phoneVerifiedStatus =
        phoneVerifiedStatus != null ? phoneVerifiedStatus : PhoneVerifiedStatus.UNVERIFIED;
    this.documentVerified = documentVerified;
    this.allowOrder = allowOrder;
    this.photoId = photoId;
    this.card = card;
    this.memo = memo;

    // 3. 개인 정보
    this.dateOfBirth = dateOfBirth;
    this.gender = gender;
    this.domestic = domestic;
    this.telecom = telecom;

    // 4. 구매 통계
    this.totalOrderCount = totalOrderCount;
    this.firstPurchased = firstPurchased;
    this.lastPurchased = lastPurchased;
    this.notPurchasedMonths = notPurchasedMonths;
    this.repurchased = repurchased;
    this.maxPrice = maxPrice;
    this.totalListPrice = totalListPrice != null ? totalListPrice : BigDecimal.ZERO;
    this.totalSellingPrice = totalSellingPrice != null ? totalSellingPrice : BigDecimal.ZERO;
    this.averagePrice = averagePrice;
    this.mileage = mileage != null ? mileage : BigDecimal.ZERO;
  }

  public static Profile of(User user) {
    return Profile.builder()
        .user(user)
        // 기본 정보 초기값 설정
        .address("")
        .phone("")
        .phoneVerified(false)
        .phoneVerifiedStatus(PhoneVerifiedStatus.UNVERIFIED)
        .documentVerified(false)
        .allowOrder(false)
        .photoId("")
        .card("")
        .memo("")

        // 개인 정보
        .gender(Gender.MALE)
        .domestic(Domestic.DOMESTIC)
        .telecom("")

        // 구매 통계 (NOT NULL인 필드들)
        .totalOrderCount(0)
        .notPurchasedMonths(false)
        .maxPrice(BigDecimal.ZERO)
        .totalListPrice(BigDecimal.ZERO)
        .totalSellingPrice(BigDecimal.ZERO)
        .averagePrice(BigDecimal.ZERO)
        .mileage(BigDecimal.ZERO)

        // 날짜 관련 필드들은 NULL 허용이므로 기본값 설정 불필요
        // - first_purchased (nullable)
        // - last_purchased (nullable)
        // - repurchased (nullable)
        // - date_of_birth (nullable)
        .build();
  }

  public ProfileEntity toEntity() {
    return ProfileEntity.builder()
        // 1. 불변 필드
        .id(this.getId())
        .user(this.getUser().toEntity())

        // 2. 기본 정보
        .phone(this.getPhone())
        .address(this.getAddress())
        .phoneVerified(this.isPhoneVerified())
        .phoneVerifiedStatus(this.getPhoneVerifiedStatus())
        .documentVerified(this.isDocumentVerified())
        .allowOrder(this.isAllowOrder())
        .photoId(this.getPhotoId())
        .card(this.getCard())
        .memo(this.getMemo())

        // 3. 개인 정보
        .dateOfBirth(this.getDateOfBirth())
        .gender(this.getGender())
        .domestic(this.getDomestic())
        .telecom(this.getTelecom())

        // 4. 구매 통계
        .totalOrderCount(this.getTotalOrderCount())
        .firstPurchased(this.getFirstPurchased())
        .lastPurchased(this.getLastPurchased())
        .notPurchasedMonths(this.isNotPurchasedMonths())
        .repurchased(this.getRepurchased())
        .maxPrice(this.getMaxPrice())
        .totalListPrice(this.getTotalListPrice())
        .totalSellingPrice(this.getTotalSellingPrice())
        .averagePrice(this.getAveragePrice())
        .mileage(this.getMileage())
        .build();
  }

  // Phone & Document Verification
  public void verifyPhoneWithStatus(PhoneVerifiedStatus status) {
    switch (status) {
      case VERIFIED -> verifyPhone(this.phone);
      case UNVERIFIED, REVOKED -> revokePhoneVerification();
      default -> throw new BusinessException(ErrorCode.INVALID_PHONE_VERIFICATION_STATUS);
    }
  }

  public void validatePhoneVerification(String phone) {
    if (phone == null || phone.isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_PHONE_NUMBER);
    }
  }

  public void verifyPhone(String phone) {
    validatePhoneVerification(phone);
    this.phone = phone;
    this.phoneVerified = true;
    this.phoneVerifiedStatus = PhoneVerifiedStatus.VERIFIED;
  }

  public void revokePhoneVerification() {
    this.phoneVerified = false;
    this.phoneVerifiedStatus = PhoneVerifiedStatus.REVOKED;
  }

  public void validateDocumentVerification() {
    if (!this.phoneVerified) {
      throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
    }
  }

  public void verifyDocument() {
    validateDocumentVerification();
    this.documentVerified = true;
  }

  public void revokeDocumentVerification() {
    this.documentVerified = false;
  }

  // Order & Purchase Management
  public void validateOrderPermission() {
    if (!canOrder()) {
      throw new BusinessException(ErrorCode.ORDER_NOT_ALLOWED);
    }
  }

  public void validatePurchaseAmount(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BusinessException(ErrorCode.INVALID_PURCHASE_AMOUNT);
    }
  }

  public void validateMileageDeduction(BigDecimal amount) {
    if (this.mileage.compareTo(amount) < 0) {
      throw new BusinessException(ErrorCode.INSUFFICIENT_MILEAGE);
    }
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

  public void updatePersonalInfo(
      LocalDate dateOfBirth, Gender gender, Domestic domestic, String telecom) {
    this.dateOfBirth = dateOfBirth;
    this.gender = gender;
    this.domestic = domestic;
    this.telecom = telecom;
  }

  // Purchase & Mileage Recording
  public void recordPurchase(BigDecimal listPrice, BigDecimal sellingPrice) {
    validatePurchaseAmount(listPrice);
    validatePurchaseAmount(sellingPrice);

    this.totalOrderCount++;
    this.totalListPrice = this.totalListPrice.add(listPrice);
    this.totalSellingPrice = this.totalSellingPrice.add(sellingPrice);
    this.averagePrice =
        this.totalSellingPrice.divide(BigDecimal.valueOf(totalOrderCount), 2, RoundingMode.HALF_UP);

    if (sellingPrice.compareTo(this.maxPrice != null ? this.maxPrice : BigDecimal.ZERO) > 0) {
      this.maxPrice = sellingPrice;
    }

    LocalDateTime now = LocalDateTime.now();
    if (this.firstPurchased == null) {
      this.firstPurchased = now;
    }
    this.lastPurchased = now;

    if (this.notPurchasedMonths) {
      this.repurchased = now;
    }
    this.notPurchasedMonths = false;
  }

  public void addMileage(BigDecimal amount) {
    validatePurchaseAmount(amount);
    this.mileage = this.mileage.add(amount);
  }

  public void subtractMileage(BigDecimal amount) {
    validateMileageDeduction(amount);
    this.mileage = this.mileage.subtract(amount);
  }

  public void updateMemo(String memo) {
    this.memo = memo;
  }

  // Status & Statistics Methods
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
    return this.getTotalDiscount()
        .divide(this.totalListPrice, 4, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100))
        .doubleValue();
  }

  public boolean needsPurchaseHistory() {
    return this.firstPurchased == null;
  }

  public boolean isLongTermCustomer() {
    return this.firstPurchased != null
        && Period.between(this.firstPurchased.toLocalDate(), LocalDate.now()).getYears() >= 1;
  }

  public boolean hasHighValuePurchase() {
    return this.maxPrice != null && this.maxPrice.compareTo(new BigDecimal("1000000")) >= 0;
  }
}
