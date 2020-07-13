package com.yimao.cloud.base.enums;

/**
 * 服务人员变更记录 - 工单类型 1-安装工单、2-维修工单、3-维护工单、4-退机工单、5-移机工单（拆机） 6-移机工单（装机）
 *
 * @author Liu Long Jie
 */
public enum ServiceEngineerChangeWorkOrderTypeEnum {

    INSTALL_WORK_ORDER("安装工单", 1),
    REPAIR_WORK_ORDER("维修工单", 2),
    MAINTENANCE_WORK_ORDER("维护工单", 3),
    BACK_WORK_ORDER("退机工单", 4),
    MOVE_WATER_DEVICE_WORK_ORDER_DISMANTLE("移机工单（拆机）", 5),
    MOVE_WATER_DEVICE_WORK_ORDER_INSTALL("移机工单（装机）", 6),
    ;

    public final String name;
    public final Integer value;

    ServiceEngineerChangeWorkOrderTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }


}
