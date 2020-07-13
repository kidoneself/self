package com.yimao.cloud.base.enums;

/**
 * 文件上传目录
 *
 * @author Zhang Bo
 * @date 2019/3/13.
 */
public enum UploadFolder {

    COMMON("common"),
    APP("app"),
    CAT("cat"),
    CMS("cms"),
    HRA("hra"),
    ORDER("order"),
    PRODUCT("product"),
    STATION("station"),
    SYSTEM("system"),
    USER("user"),
    WATER("water"),
    WECHAT("wechat"),
    SUGGEST("suggest"), //建议反馈及其回复专用目录
    ;

    public final String name;

    UploadFolder(String name) {
        this.name = name;
    }
}
