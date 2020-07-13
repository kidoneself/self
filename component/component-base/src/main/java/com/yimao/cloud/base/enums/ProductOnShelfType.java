package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 描述：产品上架类型：1-立即上架；2-自定义上架时间；3-暂不上架；
 *
 * @auther: Zhang Bo
 * @date: 2019/3/19
 */
public enum ProductOnShelfType {

    ONSHELFNOW("立即上架", 1),
    SELECTTIME("自定义上架时间", 2),
    NOTONSHELF("暂不上架", 3);

    public final String name;
    public final int value;

    ProductOnShelfType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static ProductOnShelfType find(Integer value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

}
