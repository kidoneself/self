package com.yimao.cloud.base.enums;

/**
 * 各类环境
 *
 * @author Zhang Bo
 * @date 2019/1/9.
 */
public enum EnvironmentEnum {

    LOCAL("本地环境", "local"), DEV("开发环境", "dev"), TEST("测试环境", "test"), PRO("生产环境", "pro");

    public final String name;
    public final String code;

    EnvironmentEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

}
