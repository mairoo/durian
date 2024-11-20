package kr.co.pincoin.api.global.constant;

import kr.co.pincoin.api.global.exception.ErrorCode;

public class CookieKey {
    public static final String REFRESH_TOKEN_NAME = "refresh_token";

    public static final String PATH = "/auth";

    public static final String SAME_SITE = "Strict";

    private CookieKey() {
        throw new IllegalStateException(ErrorCode.NO_INSTANCE_ALLOWED.getMessage());
    }
}
