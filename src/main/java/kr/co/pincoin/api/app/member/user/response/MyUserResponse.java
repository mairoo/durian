package kr.co.pincoin.api.app.member.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.auth.model.phone.enums.Domestic;
import kr.co.pincoin.api.domain.auth.model.phone.enums.Gender;
import kr.co.pincoin.api.domain.auth.model.phone.enums.PhoneVerifiedStatus;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyUserResponse extends UserResponse {
  // User 정보 - 개인정보
  @JsonProperty("email")
  private final String email;

  @JsonProperty("firstName")
  private final String firstName;

  @JsonProperty("lastName")
  private final String lastName;

  @JsonProperty("dateJoined")
  private final LocalDateTime dateJoined;

  @JsonProperty("lastLogin")
  private final LocalDateTime lastLogin;

  // Profile 정보 - 기본 정보
  @JsonProperty("phone")
  private final String phone;

  @JsonProperty("address")
  private final String address;

  @JsonProperty("phoneVerified")
  private final boolean phoneVerified;

  @JsonProperty("phoneVerifiedStatus")
  private final PhoneVerifiedStatus phoneVerifiedStatus;

  @JsonProperty("documentVerified")
  private final boolean documentVerified;

  @JsonProperty("allowOrder")
  private final boolean allowOrder;

  // Profile 정보 - 개인 정보
  @JsonProperty("dateOfBirth")
  private final LocalDate dateOfBirth;

  @JsonProperty("age")
  private final int age;

  @JsonProperty("gender")
  private final Gender gender;

  @JsonProperty("domestic")
  private final Domestic domestic;

  @JsonProperty("telecom")
  private final String telecom;

  protected MyUserResponse(Profile profile) {
    super(profile.getUser());

    User user = profile.getUser();

    // User 정보
    this.email = user.getEmail();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.dateJoined = user.getDateJoined();
    this.lastLogin = user.getLastLogin();

    // Profile 정보 - 기본 정보
    this.phone = profile.getPhone();
    this.address = profile.getAddress();
    this.phoneVerified = profile.isPhoneVerified();
    this.phoneVerifiedStatus = profile.getPhoneVerifiedStatus();
    this.documentVerified = profile.isDocumentVerified();
    this.allowOrder = profile.isAllowOrder();

    // Profile 정보 - 개인 정보
    this.dateOfBirth = profile.getDateOfBirth();
    this.age = profile.getAge();
    this.gender = profile.getGender();
    this.domestic = profile.getDomestic();
    this.telecom = profile.getTelecom();
  }

  public static MyUserResponse from(Profile profile) {
    return new MyUserResponse(profile);
  }
}
