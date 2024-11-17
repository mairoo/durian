package kr.co.pincoin.api.domain.auth.model.phone;

import kr.co.pincoin.api.domain.auth.model.phone.enums.Domestic;
import kr.co.pincoin.api.domain.auth.model.phone.enums.Gender;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.infra.auth.entity.phone.PhoneVerificationLogEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "instanceBuilder")
    private PhoneVerificationLog(String token,
                                 String code,
                                 String reason,
                                 String cellphone,
                                 User owner) {
        this.id = null;
        this.token = token;
        this.code = code;
        this.reason = reason;
        this.resultCode = null;
        this.message = null;
        this.transactionId = null;
        this.di = null;
        this.ci = null;
        this.fullname = null;
        this.dateOfBirth = null;
        this.gender = null;
        this.domestic = null;
        this.telecom = null;
        this.cellphone = cellphone;
        this.returnMessage = null;
        this.owner = owner;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "jpaBuilder")
    private PhoneVerificationLog(Long id,
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

    public static PhoneVerificationLog of(String token,
                                          String code,
                                          String reason,
                                          String cellphone,
                                          User owner) {
        return PhoneVerificationLog.instanceBuilder()
                .token(token)
                .code(code)
                .reason(reason)
                .cellphone(cellphone)
                .owner(owner)
                .build();
    }

    public static PhoneVerificationLog from(PhoneVerificationLogEntity entity) {
        return PhoneVerificationLog.jpaBuilder()
                .id(entity.getId())
                .token(entity.getToken())
                .code(entity.getCode())
                .reason(entity.getReason())
                .resultCode(entity.getResultCode())
                .message(entity.getMessage())
                .transactionId(entity.getTransactionId())
                .di(entity.getDi())
                .ci(entity.getCi())
                .fullname(entity.getFullname())
                .dateOfBirth(entity.getDateOfBirth())
                .gender(entity.getGender())
                .domestic(entity.getDomestic())
                .telecom(entity.getTelecom())
                .cellphone(entity.getCellphone())
                .returnMessage(entity.getReturnMessage())
                .owner(User.from(entity.getOwner()))
                .created(entity.getCreated())
                .modified(entity.getModified())
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