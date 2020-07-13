package com.yimao.cloud.base.enums;

/**
 * 描述：工单类型（安装工调用）
 *
 * @author Liu Yi
 * @date 2019/3/19.
 */
public enum WorkOrderTypeEnum {
    ORDER_TYPE_ALL("-1", "所有工单", ""),
    ORDER_TYPE_INSTALL("0", "安装单", "1"),
    ORDER_TYPE_REPAIR("1", "维修单", "2"),
    ORDER_TYPE_MAINTENANCE("2", "维护单", "3"),
    ORDER_TYPE_BACK("3", "退机单", "5"),
    ORDER_TYPE_EXCHANGE("4", "换机单", ""),
    ORDER_TYPE_RENEW("10", "续费单", "");

    private String type;
    private String typeName;
    private String colorConfigWorkOrderTypeIndex;

    private WorkOrderTypeEnum(String type, String typeName, String colorConfigWorkOrderTypeIndex) {
        this.type = type;
        this.typeName = typeName;
        this.colorConfigWorkOrderTypeIndex = colorConfigWorkOrderTypeIndex;
    }

    public static WorkOrderTypeEnum getByType(String type) {
        WorkOrderTypeEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            WorkOrderTypeEnum workOrderTypeEnum = var1[var3];
            if (type.equals(workOrderTypeEnum.getType())) {
                return workOrderTypeEnum;
            }
        }

        return null;
    }

    public static WorkOrderTypeEnum getByBaiDeIndex(String index) {
        WorkOrderTypeEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            WorkOrderTypeEnum workOrderTypeEnum = var1[var3];
            if (index.equals(workOrderTypeEnum.getColorConfigWorkOrderTypeIndex())) {
                return workOrderTypeEnum;
            }
        }

        return null;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getColorConfigWorkOrderTypeIndex() {
        return this.colorConfigWorkOrderTypeIndex;
    }

    public void setColorConfigWorkOrderTypeIndex(String colorConfigWorkOrderTypeIndex) {
        this.colorConfigWorkOrderTypeIndex = colorConfigWorkOrderTypeIndex;
    }
}
