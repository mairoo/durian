package kr.co.pincoin.api.global.constant;

import kr.co.pincoin.api.global.exception.ErrorCode;

public class CookieKey {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private CookieKey() {
        throw new IllegalStateException(ErrorCode.NO_INSTANCE_ALLOWED.getMessage());
    }
}
