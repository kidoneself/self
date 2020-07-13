package com.yimao.cloud.base.enums;

public enum WorkOrderDistributorVerifyEnum {
    BACKORDER_AUTO(1, "自动退单"),
    BACKORDER_AGREE(2, "同意退单"),
    BACKORDER_CONTINUE(3, "继续安装");

    private int verifyType;
    private String verifyTypeText;

    private WorkOrderDistributorVerifyEnum(int verifyType, String verifyTypeText) {
        this.verifyType = verifyType;
        this.verifyTypeText = verifyTypeText;
    }

    public int getVerifyType() {
        return this.verifyType;
    }

    public void setVerifyType(int verifyType) {
        this.verifyType = verifyType;
    }

    public String getVerifyTypeText() {
        return this.verifyTypeText;
    }

    public void setVerifyTypeText(String verifyTypeText) {
        this.verifyTypeText = verifyTypeText;
    }
}
