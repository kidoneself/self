package com.yimao.cloud.water.enums;

/**
 * 播放状态。
 *
 * @author Zhang Bo
 * @date 2019/2/15.
 */
public enum PlayStatusEnum {

    ONE("尚未播放", 1),
    TWO("正在播放", 2),
    THREE("自动下架", 3),
    FOUR("手动下架", 4);

    public final String name;
    public final Integer value;

    PlayStatusEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

}
