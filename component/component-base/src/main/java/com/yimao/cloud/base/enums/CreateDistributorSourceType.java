package com.yimao.cloud.base.enums;


import java.util.Arrays;

/**
 * 经销商来源方式：1-翼猫企业二维码;2-翼猫后台上线; 3:经销商app创建账号; 4-资讯分享 ; 5-视频分享; 6-权益卡分享 ; 7-发展经销商二维码；
 *
 * @author hhf
 * @date 2019/5/15
 */
public enum  CreateDistributorSourceType {

    ENTERPRISE_QR_CODE("翼猫企业二维码", 1),
    SYSTEM("翼猫后台上线", 2),
    APP("经销商app创建账号", 3),
    INFORMATION_SHARE("资讯分享", 4),
    VIDEO_SHARE("视频分享", 5),
    EQUITY_CARD_SHARE("权益卡分享", 6),
    DISTRIBUTOR_QR_CODE("发展经销商二维码", 7);

    public final String name;
    public final int value;

    CreateDistributorSourceType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static CreateDistributorSourceType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }
}
