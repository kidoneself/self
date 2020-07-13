package com.yimao.cloud.base.enums;

public enum WorkOrderChargebackEnum {
    BACK_WAIT("3", "待退单"),
    BACK_WAIT_YUN("0", "待退单"),
    BACKING("1", "退单中"),
    BACK_COMPLETE("2", "已退单");

    private String state;
    private String stateText;

    WorkOrderChargebackEnum(String state, String stateText) {
        this.state = state;
        this.stateText = stateText;
    }

    public static WorkOrderChargebackEnum getWorkBackText(String state) {
        WorkOrderChargebackEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            WorkOrderChargebackEnum backEnum = var1[var3];
            if (backEnum.getState().equals(state)) {
                return backEnum;
            }
        }

        return null;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateText() {
        return this.stateText;
    }

    public void setStateText(String stateText) {
        this.stateText = stateText;
    }
}
