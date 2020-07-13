package com.yimao.cloud.base.enums;


public enum VideoOperationEnum {

    /**
     * 服务站查询hra条件
     * 0 不限，1 没有 2 有
     */
    LIKE("视频点赞", "like"),
    SHARE("视频分享", "share"),
    WATCH("视频观看", "watch");

    public final String name;
    public final String value;

    VideoOperationEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }


}
