package com.yimao.cloud.base.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * HRA 分享类型
 *
 * @author liuhao@yimaokeji.com
 */
public enum HraShareTypeEnum {

    /**
     * 状态 0-个人二维码  1-评估卡二维码(Y) 2-评估劵二维码(Y) 3-优惠劵分享二维码(M)  4-批量分享二维码(M)
     */
    HRA_SHARE_OWNER("个人分享", 0),
    HRA_SHARE_CARD("评估卡分享", 1),
    HRA_SHARE_CARD_TICKET("评估劵分享", 2),
    HRA_SHARE_TICKET("优惠劵分享", 3),
    HRA_SHARE_BATCH("批量优惠劵分享", 4);

    public final String name;
    public final Integer value;

    HraShareTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public static HraShareTypeEnum find(Integer value) {
        return Arrays.stream(values()).filter(item -> Objects.equals(item.value, value)).findFirst().orElse(null);
    }
}
