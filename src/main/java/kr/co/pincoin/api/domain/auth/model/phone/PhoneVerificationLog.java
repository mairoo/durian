package kr.co.pincoin.api.domain.auth.model.phone;

import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.auth.model.phone.enums.Domestic;
import kr.co.pincoin.api.domain.auth.model.phone.enums.Gender;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.infra.auth.entity.phone.PhoneVerificationLogEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PhoneVerificationLog {
  private final Long id;

  private final User owner;

  private final String token;

  private final String code;

  private final String reason;

  private final String resultCode;

  private final String message;

  private final String transactionId;

  private final String di;

  private final String ci;

  private final String fullname;

  private final String dateOfBirth;

  private final Gender gender;

  private final Domestic domestic;

  private final String telecom;

  private final String cellphone;

  private final String returnMessage;

  private final LocalDateTime created;

  private final LocalDateTime modified;

  @Builder
  private PhoneVerificationLog(
      Long id,
      String token,
      String code,
      String reason,
      String resultCode,
      String message,
      String transactionId,
      String di,
      String ci,
      String fullname,
      String dateOfBirth,
      Gender gender,
      Domestic domestic,
      String telecom,
      String cellphone,
      String returnMessage,
      User owner,
      LocalDateTime created,
      LocalDateTime modified) {
    this.id = id;
    this.token = token;
    this.code = code;
    this.reason = reason;
    this.resultCode = resultCode;
    this.message = message;
    this.transactionId = transactionId;
    this.di = di;
    this.ci = ci;
    this.fullname = fullname;
    this.dateOfBirth = dateOfBirth;
    this.gender = gender;
    this.domestic = domestic;
    this.telecom = telecom;
    this.cellphone = cellphone;
    this.returnMessage = returnMessage;
    this.owner = owner;
    this.created = created;
    this.modified = modified;
  }

  public static PhoneVerificationLog of(
      String token, String code, String reason, String cellphone, User owner) {
    return PhoneVerificationLog.builder()
        .token(token)
        .code(code)
        .reason(reason)
        .cellphone(cellphone)
        .owner(owner)
        .build();
  }

  public PhoneVerificationLogEntity toEntity() {
    return PhoneVerificationLogEntity.builder()
        .id(this.getId())
        .token(this.getToken())
        .code(this.getCode())
        .reason(this.getReason())
        .resultCode(this.getResultCode())
        .message(this.getMessage())
        .transactionId(this.getTransactionId())
        .di(this.getDi())
        .ci(this.getCi())
        .fullname(this.getFullname())
        .dateOfBirth(this.getDateOfBirth())
        .gender(this.getGender())
        .domestic(this.getDomestic())
        .telecom(this.getTelecom())
        .cellphone(this.getCellphone())
        .returnMessage(this.getReturnMessage())
        .owner(this.getOwner().toEntity())
        .build();
  }

  public boolean isVerified() {
    return this.resultCode != null && this.resultCode.equals("0000");
  }

  public boolean isExpired(int expirationMinutes) {
    return this.created.plusMinutes(expirationMinutes).isBefore(LocalDateTime.now());
  }

  public boolean matchesCode(String inputCode) {
    return this.code != null && this.code.equals(inputCode);
  }

  public boolean belongsTo(User user) {
    return this.owner.getId().equals(user.getId());
  }
}
