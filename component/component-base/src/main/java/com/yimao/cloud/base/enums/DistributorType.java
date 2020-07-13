package com.yimao.cloud.base.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * 经销商角色类型
 *
 * @auther: liu.lin
 * @date: 2019/1/22
 */
public enum DistributorType {

    PROXY("代理商", 1),
    DEALER("经销商", 2),
    BOTH("经销商+代理商", 3);

    public final String name;
    public final int value;

    DistributorType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static DistributorType find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

    public static boolean isProxy(int value) {
        return value == PROXY.value || value == BOTH.value;
    }
    
    public static boolean isDealer(int value) {
        return value == DEALER.value || value == BOTH.value;
    }
    
    public static String getDistributorTypeName(int value){
       return Objects.requireNonNull(Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null)).name;
    }

}
