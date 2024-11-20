package kr.co.pincoin.api.global.constant;

import kr.co.pincoin.api.global.exception.ErrorCode;

public final class RedisKey {
    public static final String EMAIL = "email";

    public static final String IP_ADDRESS = "ipAddress";

    private RedisKey() {
        throw new IllegalStateException(ErrorCode.NO_INSTANCE_ALLOWED.getMessage());
    }
}