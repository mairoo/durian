package kr.co.pincoin.api.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // ===== 일반 에러 =====
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다"),
  NO_INSTANCE_ALLOWED(HttpStatus.INTERNAL_SERVER_ERROR, "상수 클래스는 인스턴스화할 수 없습니다"),
  REQUEST_BODY_MISSING(HttpStatus.BAD_REQUEST, "요청 본문이 없거나 형식이 잘못되었습니다"),
  REQUEST_COOKIE_MISSING(HttpStatus.BAD_REQUEST, "요청 쿠키가 없습니다."),
  UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 타입입니다"),
  FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "업로드 파일 크기가 제한을 초과했습니다"),

  // ===== 데이터 무결성 관련 =====
  DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "데이터 제약조건을 위반했습니다"),
  DUPLICATE_KEY(HttpStatus.CONFLICT, "이미 존재하는 데이터입니다"),
  FOREIGN_KEY_VIOLATION(HttpStatus.BAD_REQUEST, "참조하는 데이터가 존재하지 않습니다"),
  INVALID_NESTED_SET_VALUES(HttpStatus.BAD_REQUEST, "왼쪽 값이 오른쪽 값보다 작아야 합니다"),
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다"),

  // ===== 인증/권한 관련 =====
  AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다"),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "인증 토큰이 만료되었습니다"),
  FORBIDDEN(HttpStatus.FORBIDDEN, "해당 리소스에 대한 권한이 없습니다"),
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다"),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다"),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 토큰입니다"),
  ORDER_NOT_ALLOWED(HttpStatus.FORBIDDEN, "주문 허용되지 않았습니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다"),

  // ===== 사용자 관리 관련 =====
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다"),
  DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다"),
  INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "잘못된 휴대폰번호 형식입니다"),
  INVALID_PHONE_VERIFICATION_STATUS(HttpStatus.BAD_REQUEST, "잘못된 휴대폰인증 상태입니다."),
  PHONE_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "휴대폰 인증이 완료되지 않았습니다"),

  // ===== 스토어 관련 =====
  BLOCK_SIZE_MUST_BE_POSITIVE(HttpStatus.BAD_REQUEST, "블록 크기는 0보다 커야 합니다"),
  CHUNK_SIZE_MUST_BE_POSITIVE(HttpStatus.BAD_REQUEST, "청크 크기는 0보다 커야 합니다"),
  STORE_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "스토어 코드는 필수입니다"),
  STORE_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "스토어 이름은 필수입니다"),
  STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "상점을 찾을 수 없습니다."),
  STORE_REQUIRED(HttpStatus.BAD_REQUEST, "스토어 정보는 필수입니다"),
  STORE_THEME_REQUIRED(HttpStatus.BAD_REQUEST, "스토어 테마는 필수입니다"),

  // ===== 상품/카테고리 관련 =====
  CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
  CATEGORY_REQUIRED(HttpStatus.BAD_REQUEST, "카테고리는 필수입니다"),
  DUPLICATE_CATEGORY_SLUG(HttpStatus.CONFLICT, "카테고리 slug 중복입니다."),
  DUPLICATE_PRODUCT_CODE(HttpStatus.CONFLICT, "상품 코드가 중복입니다."),
  INVALID_CATEGORY_SLUG(HttpStatus.BAD_REQUEST, "카테고리 슬러그는 비워둘 수 없습니다"),
  INVALID_CATEGORY_TITLE(HttpStatus.BAD_REQUEST, "카테고리 제목은 비워둘 수 없습니다"),
  INVALID_DISCOUNT_RATE(HttpStatus.BAD_REQUEST, "할인율은 0에서 100 사이여야 합니다"),
  INVALID_ORIGINAL_PRICE(HttpStatus.BAD_REQUEST, "상품 가격은 0보다 커야 합니다"),
  INVALID_PG_DISCOUNT_RATE(HttpStatus.BAD_REQUEST, "PG 할인율은 0에서 100 사이여야 합니다"),
  NEGATIVE_PRICES_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "가격은 음수가 될 수 없습니다"),
  NEGATIVE_STOCK_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "재고 수량은 음수가 될 수 없습니다"),
  PRICES_REQUIRED(HttpStatus.BAD_REQUEST, "가격 정보는 필수입니다"),
  PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다"),
  SELLING_PRICE_EXCEEDS_LIST_PRICE(HttpStatus.BAD_REQUEST, "판매가는 정가를 초과할 수 없습니다"),
  STATUS_REQUIRED(HttpStatus.BAD_REQUEST, "상태값은 필수입니다"),
  STOCK_EXCEEDS_MAX_LEVEL(HttpStatus.BAD_REQUEST, "재고 수량이 최대 재고 수준을 초과했습니다"),

  // ===== 상품 목록 관련 =====
  MAX_POSITION_MUST_BE_NON_NEGATIVE(HttpStatus.BAD_REQUEST, "최대 위치 값은 0 이상이어야 합니다"),
  POSITION_MUST_BE_NON_NEGATIVE(HttpStatus.BAD_REQUEST, "위치 값은 0 이상이어야 합니다"),
  PRODUCT_LIST_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "상품 목록 코드는 필수입니다"),
  PRODUCT_LIST_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "상품 목록 이름은 필수입니다"),
  STORE_MISMATCH(HttpStatus.BAD_REQUEST, "상품과 상품 목록의 스토어가 일치하지 않습니다"),

  // ===== 주문/결제 관련 =====
  DELETED_ORDER_CANNOT_BE_REFUNDED(HttpStatus.BAD_REQUEST, "삭제된 주문은 환불할 수 없습니다"),
  INSUFFICIENT_MILEAGE(HttpStatus.BAD_REQUEST, "마일리지가 부족합니다"),
  INVALID_PURCHASE_AMOUNT(HttpStatus.BAD_REQUEST, "잘못된 구매액입니다"),
  LIST_PRICE_MUST_BE_NON_NEGATIVE(HttpStatus.BAD_REQUEST, "정가는 0 이상이어야 합니다"),
  NEGATIVE_BALANCE(HttpStatus.BAD_REQUEST, "잔액은 음수가 될 수 없습니다"),
  ORDER_ALREADY_DELETED(HttpStatus.CONFLICT, "이미 삭제된 주문입니다"),
  ORDER_ALREADY_HIDDEN(HttpStatus.CONFLICT, "이미 숨김 처리된 주문입니다"),
  ORDER_ALREADY_REFUNDED(HttpStatus.CONFLICT, "이미 환불 처리된 주문입니다"),
  ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
  ORDER_NOT_IN_REFUND_REQUEST_STATUS(HttpStatus.BAD_REQUEST, "환불 요청 상태의 주문이 아닙니다"),
  ORDER_NOT_IN_REFUND_WAIT_STATUS(HttpStatus.BAD_REQUEST, "환불 처리 대기 상태의 주문이 아닙니다"),
  ORIGINAL_ORDER_NOT_FOUND_FOR_REFUND(HttpStatus.NOT_FOUND, "환불 처리할 원본 주문을 찾을 수 없습니다"),
  PAYMENT_ALREADY_RECEIVED(HttpStatus.CONFLICT, "이미 결제가 완료된 주문입니다"),
  PAYMENT_AMOUNT_MUST_BE_POSITIVE(HttpStatus.BAD_REQUEST, "결제 금액은 0보다 커야 합니다"),
  PAYMENT_COMPLETED_ORDER(HttpStatus.CONFLICT, "이미 결제가 완료된 주문입니다"),
  QUANTITY_MUST_BE_POSITIVE(HttpStatus.BAD_REQUEST, "수량은 0보다 커야 합니다"),
  SELLING_PRICE_MUST_BE_NON_NEGATIVE(HttpStatus.BAD_REQUEST, "판매가는 0 이상이어야 합니다"),

  // ===== 장바구니 관련 =====
  CART_DATA_REQUIRED(HttpStatus.BAD_REQUEST, "장바구니 데이터는 필수입니다"),
  INVALID_CART_DATA_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 장바구니 데이터 형식입니다"),

  // ===== 구매 주문 관련 =====
  CANNOT_REMOVE_PAID_PURCHASE_ORDER(HttpStatus.BAD_REQUEST, "결제 완료된 구매 주문은 삭제할 수 없습니다"),
  CANNOT_UPDATE_PAID_PURCHASE_ORDER(HttpStatus.BAD_REQUEST, "결제 완료된 구매 주문은 수정할 수 없습니다"),
  PURCHASE_ORDER_ALREADY_PAID(HttpStatus.CONFLICT, "이미 결제 완료된 구매 주문입니다"),
  PURCHASE_ORDER_ALREADY_UNPAID(HttpStatus.CONFLICT, "이미 미결제 상태인 구매 주문입니다"),
  PURCHASE_ORDER_AMOUNT_MUST_BE_POSITIVE(HttpStatus.BAD_REQUEST, "구매 주문 금액은 0보다 커야 합니다"),
  PURCHASE_ORDER_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "구매 주문 제목은 필수입니다"),

  // ===== 상품권 관련 =====
  ACTIVE_VOUCHER_CANNOT_BE_REMOVED(HttpStatus.BAD_REQUEST, "활성화된 상품권은 삭제할 수 없습니다"),
  DUPLICATE_VOUCHER_CODE(HttpStatus.CONFLICT, "상품권 번호가 중복입니다. [개별]"),
  DUPLICATE_VOUCHER_CODES(HttpStatus.CONFLICT, "상품권 번호가 중복입니다. [일괄]"),
  INVALID_VOUCHER_CODE_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 상품권 코드 형식입니다"),
  INVALID_VOUCHER_STATUS(HttpStatus.BAD_REQUEST, "잘못된 상품권 상태입니다."),
  VOUCHER_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "상품권 코드는 필수입니다"),
  VOUCHER_NOT_AVAILABLE_FOR_SALE(HttpStatus.BAD_REQUEST, "판매 가능한 상태의 상품권만 판매할 수 있습니다"),
  VOUCHER_NOT_FOUND(HttpStatus.NOT_FOUND, "상품권을 찾을 수 없습니다."),
  VOUCHER_REMARKS_REQUIRED(HttpStatus.BAD_REQUEST, "상품권 비고는 필수입니다"),

  // ===== 문의 관련 =====
  ANSWER_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "답변 내용은 필수입니다"),
  ANSWER_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "답변 내용은 1000자를 초과할 수 없습니다"),
  CANNOT_ANSWER_CLOSED_QUESTION(HttpStatus.BAD_REQUEST, "닫힌 문의에는 답변할 수 없습니다"),
  QUESTION_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "문의 내용은 필수입니다"),
  QUESTION_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "문의 내용은 2000자를 초과할 수 없습니다"),
  QUESTION_REQUIRED(HttpStatus.BAD_REQUEST, "문의가 필요합니다"),

  // ===== 후기 관련 =====
  CANNOT_ANSWER_HIDDEN_TESTIMONIAL(HttpStatus.BAD_REQUEST, "숨김 처리된 후기에는 답변할 수 없습니다"),
  CANNOT_ANSWER_REMOVED_TESTIMONIAL(HttpStatus.BAD_REQUEST, "삭제된 후기에는 답변할 수 없습니다"),
  TESTIMONIAL_OWNER_REQUIRED(HttpStatus.BAD_REQUEST, "후기 작성자는 필수입니다"),
  TESTIMONIAL_REQUIRED(HttpStatus.BAD_REQUEST, "후기는 필수입니다"),
  TESTIMONIAL_STORE_REQUIRED(HttpStatus.BAD_REQUEST, "후기 스토어는 필수입니다"),

  // ===== SMS 관련 =====
  INVALID_PHONE_NUMBER_LENGTH(HttpStatus.BAD_REQUEST, "전화번호는 10-11자리여야 합니다"),
  INVALID_SMS_TYPE(HttpStatus.BAD_REQUEST, "잘못된 SMS 타입입니다"),
  PHONE_NUMBER_REQUIRED(HttpStatus.BAD_REQUEST, "전화번호는 필수입니다"),
  SMS_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "SMS 내용은 필수입니다"),
  SMS_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "SMS 내용은 2000자를 초과할 수 없습니다"),

  // ===== 공지사항/FAQ 관련 =====
  MESSAGE_CATEGORY_REQUIRED(HttpStatus.BAD_REQUEST, "메시지 카테고리는 필수입니다"),
  MESSAGE_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "메시지 내용은 필수입니다"),
  MESSAGE_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "메시지 내용은 2000자를 초과할 수 없습니다"),
  MESSAGE_OWNER_REQUIRED(HttpStatus.BAD_REQUEST, "메시지 작성자는 필수입니다"),
  MESSAGE_STORE_REQUIRED(HttpStatus.BAD_REQUEST, "메시지 스토어는 필수입니다"),
  MESSAGE_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "메시지 제목은 필수입니다"),
  MESSAGE_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "메시지 제목은 200자를 초과할 수 없습니다"),

  // ===== 외부 서비스 연동 =====
  ALIGO_API_PARSE_ERROR(HttpStatus.BAD_REQUEST, "알리고 API 응답 파싱 실패"),
  ALIGO_API_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알리고 SMS 발송 실패"),
  EMAIL_TEMPLATE_ALREADY_EXISTS(HttpStatus.CONFLICT, "동일한 이름의 이메일 템플릿이 이미 존재합니다."),
  EMAIL_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "이메일 템플릿을 찾을 수 없습니다."),
  EXTERNAL_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "외부 서비스 연동 중 오류가 발생했습니다"),
  GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "외부 서비스 응답 시간을 초과했습니다"),
  MAILGUN_API_PARSE_ERROR(HttpStatus.BAD_REQUEST, "mailgun API 응답 파싱 오류"),
  MAILGUN_API_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "mailgun API 발송 오류");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}