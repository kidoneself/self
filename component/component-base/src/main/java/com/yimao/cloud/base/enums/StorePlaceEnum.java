package com.yimao.cloud.base.enums;

/**
 * @author zhilin.he
 * @description 仓库位置类型
 * @date 2019/5/6 15:16
 */
public enum StorePlaceEnum {

    SEPARATE_STORE("分仓", 0),
    MAIN_STORE("总仓", 1);

    public final String name;
    public final int value;

    StorePlaceEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
