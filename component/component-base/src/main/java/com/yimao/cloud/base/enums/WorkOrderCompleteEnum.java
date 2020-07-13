package com.yimao.cloud.base.enums;

public enum WorkOrderCompleteEnum {
    NORMAL_COMPLETE(1, "正常完成"),
    AUTO_COMPLETE(2, "非正常完成");

    private int state;
    private String stateText;

    WorkOrderCompleteEnum(int state, String stateText) {
        this.state = state;
        this.stateText = stateText;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateText() {
        return this.stateText;
    }

    public void setStateText(String stateText) {
        this.stateText = stateText;
    }
}
