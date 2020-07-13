package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * @Author ycl
 * @Description 来源方式
 * @Date 11:17 2019/7/13
**/
public enum OriginEnum {

    ENDORSEMENT_CARD("代言卡分享", 1),
    OUTREACH_CARD("宣传卡分享", 2),
    INFORMATION_SHARE("资讯分享", 3),
    VIDEO_SHARE("视频分享", 4),
    GOODS_SHARE("商品分享", 5),
    FOCUS_ON("自主关注公众号", 6),
    DISTRIBUTOR_APP("经销商APP添加", 7),
    DISABLE_SCREEN("水机屏推广二维码", 10),
    DISTRIBUTOR_MOBILE_REGISTER("经销商APP手机号注册",11),
    DISTRIBUTOR_WECHAT_REGISTER("经销商APP微信授权注册",12),
    H5_SHARE("H5分享",21),
	YIMAO_BACK_ONLINE("翼猫后台上线",22);

    public final String name;
    public final int value;

    OriginEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static OriginEnum find(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }


}
