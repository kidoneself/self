package com.yimao.cloud.base.enums;

/**
 * 使用状态/0-使用、 1-禁用
 *
 * @author Zhang Bo
 * @date 2018/10/23.
 */
public enum DistributorConfigurationUseStateEnum {

    /**
     *
     * 0-使用、禁用
     */
    Disable("禁用", 0),
    Enable("启用", 1);

    public final String name;
    public final int value;

    DistributorConfigurationUseStateEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
