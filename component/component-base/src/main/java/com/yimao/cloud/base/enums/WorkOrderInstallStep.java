package com.yimao.cloud.base.enums;

/**
 * 工单安装步骤枚举类
 */
public enum WorkOrderInstallStep {

    START(0, "第一步"),
    OUTSTOCK(1, "缺货"),
    PICK(2, "提货完成"),
    PROTOCOL(3, "输入完编号"),
    SNCODE(4, "输入完sn码"),
    SIMCARD(5, "输入完SIM卡"),
    PAID(6, "支付完成"),
    MSG(7, "个人信息提交"),
    BILL(8, "发票"),
    APPRAISE(9, "评价完成"),
    ELEVEN(11, "微服务中完成状态");

    public int value;
    public String name;

    WorkOrderInstallStep(int value, String name) {
        this.value = value;
        this.name = name;
    }

}
