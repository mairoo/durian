package kr.co.pincoin.api.app.admin.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.app.member.user.response.MyUserResponse;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminUserResponse extends MyUserResponse {
  // User 정보 - 관리자용 필드
  @JsonProperty("isSuperuser")
  private final boolean isSuperuser;

  @JsonProperty("isStaff")
  private final boolean isStaff;

  // Profile 정보 - 관리자용 필드
  @JsonProperty("photoId")
  private final String photoId;

  @JsonProperty("card")
  private final String card;

  @JsonProperty("memo")
  private final String memo;

  @JsonProperty("notPurchasedMonths")
  private final boolean notPurchasedMonths;

  // Profile 정보 - 구매 통계
  @JsonProperty("totalOrderCount")
  private final int totalOrderCount;

  @JsonProperty("firstPurchased")
  private final LocalDateTime firstPurchased;

  @JsonProperty("lastPurchased")
  private final LocalDateTime lastPurchased;

  @JsonProperty("repurchased")
  private final LocalDateTime repurchased;

  @JsonProperty("maxPrice")
  private final BigDecimal maxPrice;

  @JsonProperty("totalListPrice")
  private final BigDecimal totalListPrice;

  @JsonProperty("totalSellingPrice")
  private final BigDecimal totalSellingPrice;

  @JsonProperty("averagePrice")
  private final BigDecimal averagePrice;

  @JsonProperty("totalDiscount")
  private final BigDecimal totalDiscount;

  @JsonProperty("averageDiscountRate")
  private final double averageDiscountRate;

  @JsonProperty("mileage")
  private final BigDecimal mileage;

  @JsonProperty("isRegularCustomer")
  private final boolean isRegularCustomer;

  private AdminUserResponse(Profile profile) {
    super(profile);

    User user = profile.getUser();

    // User 정보
    this.isSuperuser = user.isSuperuser();
    this.isStaff = user.isStaff();

    // Profile 정보 - 관리자용 기본 정보
    this.photoId = profile.getPhotoId();
    this.card = profile.getCard();
    this.memo = profile.getMemo();
    this.notPurchasedMonths = profile.isNotPurchasedMonths();

    // Profile 정보 - 구매 통계
    this.totalOrderCount = profile.getTotalOrderCount();
    this.firstPurchased = profile.getFirstPurchased();
    this.lastPurchased = profile.getLastPurchased();
    this.repurchased = profile.getRepurchased();
    this.maxPrice = profile.getMaxPrice();
    this.totalListPrice = profile.getTotalListPrice();
    this.totalSellingPrice = profile.getTotalSellingPrice();
    this.averagePrice = profile.getAveragePrice();
    this.totalDiscount = profile.getTotalDiscount();
    this.averageDiscountRate = profile.getAverageDiscountRate();
    this.mileage = profile.getMileage();
    this.isRegularCustomer = profile.isRegularCustomer();
  }

  public static AdminUserResponse from(Profile profile) {
    return new AdminUserResponse(profile);
  }
}
