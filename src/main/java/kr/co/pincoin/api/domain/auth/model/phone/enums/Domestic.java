package kr.co.pincoin.api.domain.auth.model.phone.enums;

import lombok.Getter;

@Getter
public enum Domestic {
    FOREIGN(0, "외국인"),
    DOMESTIC(1, "내국인");

    private final int code;
    private final String description;

    Domestic(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
