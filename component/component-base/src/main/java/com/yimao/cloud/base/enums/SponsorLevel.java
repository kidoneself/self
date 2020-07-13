package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 发起人级别：1-省级；2-市级；4-区级；3-省级+市级；5-省级+区级；6-市级+区级；7-省级+市级+区级；
 *
 * @author Lizhqiang
 * @date 2019/2/28
 */
public enum SponsorLevel {

    SPONSOR_P("省级", 1),
    SPONSOR_C("市级", 2),
    SPONSOR_R("区级", 4),
    SPONSOR_PC("省级+市级", 3),
    SPONSOR_PR("省级+区级", 5),
    SPONSOR_CR("市级+区级", 6),
    SPONSOR_PCR("省级+市级+区级", 7);

    public final String name;
    public final int value;

    SponsorLevel(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static SponsorLevel find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

    //是否是省级
    public static boolean isSponsorProvince(int value) {
        return value == SPONSOR_P.value || value == SPONSOR_PC.value || value == SPONSOR_PR.value || value == SPONSOR_PCR.value;
    }

    //是否是市级
    public static boolean isSponsorCity(int value) {
        return value == SPONSOR_C.value || value == SPONSOR_PC.value || value == SPONSOR_CR.value || value == SPONSOR_PCR.value;
    }

    //是否是区级
    public static boolean isSponsorRegion(int value) {
        return value == SPONSOR_R.value || value == SPONSOR_PR.value || value == SPONSOR_CR.value || value == SPONSOR_PCR.value;
    }
}
