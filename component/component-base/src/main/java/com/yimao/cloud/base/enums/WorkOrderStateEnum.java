package com.yimao.cloud.base.enums;

public enum WorkOrderStateEnum {
    WORKORDER_STATE_NOT_ACCEPTED(1, "未受理"),
    WORKORDER_STATE_ACCEPTED(2, "已受理"),
    WORKORDER_STATE_SERVING(3, "处理中"),
    WORKORDER_STATE_COMPLETE(4, "已完成"),
    WORKORDER_REPAIR_CHANGE_DEVICE_INIT(-1, ""),
    WORKORDER_REPAIR_CHANGE_DEVICE_APPLYING(1, "审核中"),
    WORKORDER_REPAIR_CHANGE_DEVICE_PASS(2, "审核通过"),
    WORKORDER_REPAIR_CHANGE_DEVICE_REJECT(3, "审核驳回"),

    //原微服务提供安装工app值 --- start
    NOT_ACCEPTED(1, "未受理"),
    HANDLING(2, "处理中"),
    COMPLETE(3, "已完成"),
    //原微服务提供安装工app值 --- end

    PAY(0, "已支付"),
    WAITING_PAY(5, "待付款"),
    CUSTOMER_REJECT(6, "客服拒绝"),
    CUSTOMER_DISPATCH(7, "分配客服"),
    AUDITING(-1, "审核中"),
    NOT_PASS(-2, "审核未通过"),
    STATUS(-3, "状态");


    public int state;
    public String stateText;

    WorkOrderStateEnum(int state, String stateText) {
        this.state = state;
        this.stateText = stateText;
    }

    public static int getStateByStatus(int status) {
        switch (status) {
            case -2:
            case -1:
            case 0:
            case 2:
            case 3:
            case 5:
            case 6:
            case 7:
                return HANDLING.state;
            case 1:
                return NOT_ACCEPTED.state;
            case 4:
                return COMPLETE.state;
            default:
                return 100;
        }
    }

}
