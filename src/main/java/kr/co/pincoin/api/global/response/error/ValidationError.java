package kr.co.pincoin.api.global.response.error;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidationError {
    private final String field;
    private final String message;
}