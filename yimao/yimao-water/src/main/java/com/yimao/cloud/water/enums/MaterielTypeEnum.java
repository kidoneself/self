package com.yimao.cloud.water.enums;

/**
 * 描述：视频，图片。
 *
 * @Author Zhang Bo
 * @Date 2019/2/21 11:26
 * @Version 1.0
 */
public enum MaterielTypeEnum {

    ONE("视频", 1),
    TWO("图片", 2),
    THREE("链接", 3);

    public final String name;
    public final int value;

    MaterielTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
