package com.yimao.cloud.base.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * HRA 发放类型
 *
 * @author liuhao@yimaokeji.com
 */
public enum HraGiveTypeEnum {

    /**
     * 体检卡状态
     * 1-经销商固定配额，2-经销商业绩发放，3-用户业绩，5-手动发放卡，7-分销用户业绩卡 8-兑换码兑换
     */
    DIS_QUOTA("经销商固定配额", 1),
    DIS_SALE("经销商业绩发放", 2),
    USER_SALE("用户业绩发放", 3),
    USER_QUOTA("分销用户配额", 7),
    HRA_APPLY("招商申请发放", 5), //手动发放
    HRA_EXCHANGE("兑换码兑换", 8);

    public final String name;
    public final int value;

    HraGiveTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static HraGiveTypeEnum find(int value) {
        return Arrays.stream(values()).filter(item -> Objects.equals(item.value, value)).findFirst().orElse(null);
    }
}
