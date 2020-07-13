package com.yimao.cloud.water.enums;

/**
 * 水机型号。
 *
 * @author Zhang Bo
 * @date 2019/2/15.
 */
public enum ProductModelEnum {

    MODEL_ALL("所有型号", null),
    MODEL_1601T("家用型1", "1601T"),
    MODEL_1602T("家用型2", "1602T"),
    MODEL_1603T("家用型3", "1603T"),
    MODEL_1601L("商用型1", "1601L");

    public final String name;
    public final String value;

    ProductModelEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

}
