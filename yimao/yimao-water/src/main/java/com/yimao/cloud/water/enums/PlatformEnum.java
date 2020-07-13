package com.yimao.cloud.water.enums;

/**
 * 第三方平台。
 *
 * @author Zhang Bo
 * @date 2019/2/15.
 */
public enum PlatformEnum {

    YIMAO("翼猫", 0),
    BAIDU("百度", 1),
    JINGDONG("京东", 2),
    IFLYTEK("科大讯飞", 3),
    JIANSHI("简视", 4),
    HAIDA("海大", 5),
    SYSTEM("后台系统", 6),
    YISHOU("易售", 7),
    OTHER("其它", 10);

    public final String name;
    public final int value;

    PlatformEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
