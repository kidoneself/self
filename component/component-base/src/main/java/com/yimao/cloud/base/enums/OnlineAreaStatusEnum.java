package com.yimao.cloud.base.enums;

/**
 * 服务站上线新流程状态
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
public enum OnlineAreaStatusEnum {

    NOT("未上线", 1),
    IN("上线中", 2),
    DONE("上线完成", 3);

    public final String name;
    public final int value;

    OnlineAreaStatusEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
