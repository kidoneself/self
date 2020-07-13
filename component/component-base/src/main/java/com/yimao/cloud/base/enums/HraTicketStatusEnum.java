package com.yimao.cloud.base.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * HRA体检卡状态
 *
 * @author liuhao@yimaokeji.com
 */
public enum HraTicketStatusEnum {

    /**
     * 体检卡状态
     */
    HRA_PAY("待使用", 1),  //已支付待使用
    HRA_USE("已使用", 2),
    HRA_STOP("已禁用", 3),
    HRA_EXPIRE("已过期", 4),
    HRA_RESERVE("已预约", 5);

    public final String name;
    public final int value;

    HraTicketStatusEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static HraTicketStatusEnum find(int value) {
        return Arrays.stream(values()).filter(item -> Objects.equals(item.value, value)).findFirst().orElse(null);
    }
}
