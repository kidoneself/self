package com.yimao.cloud.base.enums;


/**
 * 库存管理操作类型
 *
 * @author Liu Long Jie
 * @date 2020-6-22 10:42:17
 */
public enum StoreHouseOperationTypeEnum {

    /**
     * 操作类型:
     */
    STORE_HOUSE_UPDATE_COUNT("后台修改库存", 1),
    GOODS_STOCK_DISTRIBUTION("服务站公司库存分配至服务站门店", 2),
    GOODS_STOCK_TRANSFER("后台调拨", 3),
    GOODS_STOCK_RETURN("退机转良品库存", 4),
    GOODS_STOCK_DEFECTIVE_RETURN("退机转不良品库存", 5),
    GOODS_PRUNE_STATION_STOCK("扣减服务站可用库存", 6),
    GOODS_PRUNE_STATION_OCCUPY_STOCK("扣减服务站占用库存", 7),
    GOODS_ADD_STATION_STOCK("安装工退单，增加服务站可用库存", 8),;

    public final String name;
    public final int value;

    StoreHouseOperationTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
