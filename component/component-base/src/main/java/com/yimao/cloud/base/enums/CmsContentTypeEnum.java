package com.yimao.cloud.base.enums;

/**
 *
 * @Description 内容类型枚举
 * @auther: liu.lin
 * @date: 2019/1/26
 */
public enum CmsContentTypeEnum {

    TYPE_HEADQUARTERS("总部",1),
    TYPE_DIVISION("服务站",2);

    public final String name;
    public final int value;

    CmsContentTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
}
