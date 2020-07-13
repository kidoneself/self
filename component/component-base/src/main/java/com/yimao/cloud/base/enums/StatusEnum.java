package com.yimao.cloud.base.enums;

public enum StatusEnum {
    NONE("-"),
    TRUE("Y"),
    YES("Y"),
    PROCESSING("P"),
    FALSE("N"),
    NO("N"),
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE");

    private String status;

    StatusEnum(String status) {
        this.status = status;
    }

    public String value() {
        return this.status;
    }

    public static boolean isYes(String code) {
        return code != null && code.equals(YES.value());
    }

    public static boolean isNo(String code) {
        return code != null && code.equals(NO.value());
    }
    
    public static boolean isPROCESSING(String code) {
        return code != null && code.equals(PROCESSING.value());
    }
}
