package com.yimao.cloud.base.enums;

/**
 *
 * @Description 内容状态枚举
 * @auther: liu.lin
 * @date: 2019/1/26
 */
public enum CmsContentStatusEnum {

    STATUS_PUBLISHED("已发布",1),
    STATUS_READY("待发布",2),
    STATUS_DELETE("已删除",4),
    STATUS_PENDINGREVIEW("待审核",7),
    STATUS_AUDITFAILURE("审核失败",8),;

    public final String name;
    public final int value;

    CmsContentStatusEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
}
