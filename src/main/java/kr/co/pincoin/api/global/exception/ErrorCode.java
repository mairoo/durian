package kr.co.pincoin.api.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // 비즈니스 로직
  PAYMENT_ALREADY_RECEIVED(HttpStatus.CONFLICT, "이미 결제가 완료된 주문입니다"),
  NEGATIVE_BALANCE(HttpStatus.BAD_REQUEST, "잔액은 음수가 될 수 없습니다"),

  INVALID_NESTED_SET_VALUES(HttpStatus.BAD_REQUEST, "왼쪽 값이 오른쪽 값보다 작아야 합니다"),
  INVALID_ORIGINAL_PRICE(HttpStatus.BAD_REQUEST, "상품 가격은 0보다 커야 합니다"),
  INVALID_CATEGORY_TITLE(HttpStatus.BAD_REQUEST, "카테고리 제목은 비워둘 수 없습니다"),
  INVALID_CATEGORY_SLUG(HttpStatus.BAD_REQUEST, "카테고리 슬러그는 비워둘 수 없습니다"),
  STORE_REQUIRED(HttpStatus.BAD_REQUEST, "스토어 정보는 필수입니다"),
  INVALID_DISCOUNT_RATE(HttpStatus.BAD_REQUEST, "할인율은 0에서 100 사이여야 합니다"),
  INVALID_PG_DISCOUNT_RATE(HttpStatus.BAD_REQUEST, "PG 할인율은 0에서 100 사이여야 합니다"),
  STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "상점을 찾을 수 없습니다."),
  CATEGORY_NOT_FOUND(HttpStatus.CONFLICT, "카테고리를 찾을 수 없습니다."),
  DUPLICATE_CATEGORY_SLUG(HttpStatus.BAD_REQUEST, "카테고리 slug 중복입니다."),
  PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다"),
  DUPLICATE_PRODUCT_CODE(HttpStatus.CONFLICT, "상품 코드가 중복입니다."),
  VOUCHER_NOT_FOUND(HttpStatus.NOT_FOUND, "상품권을 찾을 수 없습니다."),
  INVALID_VOUCHER_STATUS(HttpStatus.BAD_REQUEST, "잘못된 상품권 상태입니다."),
  DUPLICATE_VOUCHER_CODE(HttpStatus.CONFLICT, "상품권 번호가 중복입니다. [개별]"),
  DUPLICATE_VOUCHER_CODES(HttpStatus.CONFLICT, "상품권 번호가 중복입니다. [일괄]"),
  ORDER_NOT_FOUND(HttpStatus.FORBIDDEN, "주문을 찾을 수 없습니다."),

  // 공통
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
  REQUEST_BODY_MISSING(HttpStatus.BAD_REQUEST, "요청 본문이 없거나 형식이 잘못되었습니다"),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다"),
  UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 타입입니다"),
  REQUEST_COOKIE_MISSING(HttpStatus.BAD_REQUEST, "요청 쿠키가 없습니다."),
  FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "업로드 파일 크기가 제한을 초과했습니다"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
  NO_INSTANCE_ALLOWED(HttpStatus.INTERNAL_SERVER_ERROR, "상수 클래스는 인스턴스화할 수 없습니다"),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

  // 인증 및 권한
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 토큰입니다"),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "인증 토큰이 만료되었습니다"),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다"),
  FORBIDDEN(HttpStatus.FORBIDDEN, "해당 리소스에 대한 권한이 없습니다"),
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다"),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다"),
  INVALID_PHONE_VERIFICATION_STATUS(HttpStatus.BAD_REQUEST, "잘못된 휴대폰인증 상태입니다."),
  AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다"),
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다"),
  DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다"),
  INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "잘못된 휴대폰번호 형식입니다"),
  PHONE_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "휴대폰 인증이 완료되지 않았습니다"),
  INVALID_PURCHASE_AMOUNT(HttpStatus.BAD_REQUEST, "잘못된 구매액입니다"),
  INSUFFICIENT_MILEAGE(HttpStatus.BAD_REQUEST, "마일리지가 부족합니다"),
  ORDER_NOT_ALLOWED(HttpStatus.FORBIDDEN, "주문 허용되지 않았습니다."),

  // 리소스/엔티티 관련
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다"),
  DUPLICATE_KEY(HttpStatus.CONFLICT, "이미 존재하는 데이터입니다"),
  DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "데이터 제약조건을 위반했습니다"),
  FOREIGN_KEY_VIOLATION(HttpStatus.BAD_REQUEST, "참조하는 데이터가 존재하지 않습니다"),

  // 외부 서비스 연동
  EXTERNAL_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "외부 서비스 연동 중 오류가 발생했습니다"),
  GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "외부 서비스 응답 시간을 초과했습니다"),
  ALIGO_API_PARSE_ERROR(HttpStatus.CONFLICT, "알리고 API 응답 파싱 실패"),
  ALIGO_API_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알리고 SMS 발송 실패"),
  MAILGUN_API_PARSE_ERROR(HttpStatus.CONFLICT, "mailgun API 응답 파싱 오류"),
  MAILGUN_API_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "mailgun API 발송 오류"),
  EMAIL_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "이메일 템플릿을 찾을 수 없습니다."),
  EMAIL_TEMPLATE_ALREADY_EXISTS(HttpStatus.CONFLICT, "동일한 이름의 이메일 템플릿이 이미 존재합니다.");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}
