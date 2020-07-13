package com.yimao.cloud.base.enums;


/**
 * 库存管理操作主体
 * @author Liu Long Jie
 * @date 2020-6-22
 */
public enum StoreHouseOperationSubObjectEnum {

    /**
     * 主体:1.总仓 2、服务站门店 3-服务站公司
     */
    STORE_HOUSE_ALL("总仓", 1),
    STATION("服务站门店", 2),
    STATION_COMPANY("服务站公司", 3),
    ;

    public final String name;
    public final int value;

    StoreHouseOperationSubObjectEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
