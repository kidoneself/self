package com.yimao.cloud.base.enums;
/**
 * @Author ycl
 * @Description 售后审核方
 * @Date 15:37 2019/10/14
 * @Param
**/
public enum ReviewerEnum {

    DISTRIBUTOR("经销商", 0),
    HEADQUARTERS("总部", 1);

    public final String name;
    public final int value;

    ReviewerEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
