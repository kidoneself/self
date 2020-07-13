package com.yimao.cloud.base.enums;

/**
 * @author zhilin.he
 * @description  审核类型枚举
 * @date 2019/1/30 15:27
 **/
public enum AuditSubTypeEnum {

    /**
     *  子审核类型  1-业务部门审核，2-400客服审核（租赁商品），3-400客服提交物流，4-物资审核
     */

    BUSINESS_AUDIT("BUSINESS_AUDIT", 1),
    CUSTOMER_SERVICE_AUDIT("CUSTOMER_SERVICE_AUDIT", 2),
    CUSTOMER_SERVICE_LOGISTICS("CUSTOMER_SERVICE_LOGISTICS", 3),
    MATERIAL_AUDIT("MATERIAL_AUDIT", 4);

    public final String name;
    public final int value;

    AuditSubTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
