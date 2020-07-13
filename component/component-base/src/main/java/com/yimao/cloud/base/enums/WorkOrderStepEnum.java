package com.yimao.cloud.base.enums;

public enum WorkOrderStepEnum {
    MAINTENANCE_WORK_ORDER_STEP_INIT(-1, "工单初始化步骤", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE.getType())),
    /**
     * 维修 Step1----- 预约上门     Step2----- 开始服务     Step3----- 填写故障信息接口   Step4----- 申请换机    Step5----- 更换设备    Step6----- 完成工单
     * 维护:step :0---客服接单  step:1---客服预约    step:2---开始服务   step:3---扫描二维码   step:4---上传滤芯销毁图片 step:5---完成维护
     */
    /**
     * 维护工单步骤枚举
     */
    @Deprecated
    MAINTENANCE_WORK_ORDER_STEP_ACCEPT(0,"客服接单",Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE.getType())),
    MAINTENANCE_WORK_ORDER_STEP_APPOINTMENT(1,"客服预约",Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE.getType())),
    MAINTENANCE_WORK_ORDER_STEP_STARTSERVER(2,"客服开始服务",Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE.getType())),
    MAINTENANCE_WORK_ORDER_STEP_SCANBATCHCODE(3,"扫描滤芯二维码",Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE.getType())),
    MAINTENANCE_WORK_ORDER_STEP_UPLOAD_IMG(4,"销毁滤芯图片上传",Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE.getType())),
    MAINTENANCE_WORK_ORDER_STEP_FINISH_WORK(5,"完成维护",Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE.getType())),


    /** @deprecated */
    @Deprecated
    REPAIR_WORK_ORDER_STEP_ACCEPT(0, "客服接单", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType())),
    REPAIR_WORK_ORDER_STEP_APPOINTMENT(1, "客服预约", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType())),
    REPAIR_WORK_ORDER_STEP_STARTSERVER(2, "客服开始服务", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType())),
    REPAIR_WORK_ORDER_STEP_FACTFAULT_DESC(3, "故障信息维护", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType())),
    REPAIR_WORK_ORDER_STEP_APPLY_CHANGE_DEVICE(4, "申请换机", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType())),
    REPAIR_WORK_ORDER_STEP_CHANGE_DEVICE(5, "更换设备", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType())),
    REPAIR_WORK_ORDER_STEP_FINISH_WORK(6, "完成维修", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType())),
    /** @deprecated */
    @Deprecated
    EXCHANGE_WORK_ORDER_STEP_ACCEPT(0, "客服接单", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_EXCHANGE.getType())),
    EXCHANGE_WORK_ORDER_STEP_APPOINTMENT(1, "客服预约", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_EXCHANGE.getType())),
    EXCHANGE_WORK_ORDER_STEP_STARTSERVER(2, "客服开始服务", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_EXCHANGE.getType())),
    EXCHANGE_WORK_ORDER_STEP_EXCHANGE(3, "客服开始换机", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_EXCHANGE.getType())),
    EXCHANGE_WORK_ORDER_STEP_FINISH_WORK(4, "完成换机", Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_EXCHANGE.getType())),
    WORK_ORDER_STEP_END(100, "工单结束", 100);

    int step;
    String stepText;
    int workOrderIndex;

    private WorkOrderStepEnum(int step, String stepText, int workOrderIndex) {
        this.step = step;
        this.stepText = stepText;
        this.workOrderIndex = workOrderIndex;
    }

    public static WorkOrderStepEnum getStepByBaideNextStep(int nextStep, int workOrderIndex) {
        if (nextStep == WORK_ORDER_STEP_END.getStep()) {
            return WORK_ORDER_STEP_END;
        } else {
            WorkOrderStepEnum[] steps = values();
            WorkOrderStepEnum[] var3 = steps;
            int var4 = steps.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                WorkOrderStepEnum workOrderStepEnum = var3[var5];
                if (workOrderIndex == workOrderStepEnum.getWorkOrderIndex() && nextStep == workOrderStepEnum.getStep()) {
                    return workOrderStepEnum;
                }
            }

            return null;
        }
    }

    public WorkOrderStepEnum getStepEnumByStep(int step) {
        WorkOrderStepEnum[] var2 = values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            WorkOrderStepEnum workOrderStepEnum = var2[var4];
            if (workOrderStepEnum.getStep() == step) {
                return workOrderStepEnum;
            }
        }

        return null;
    }

    public int getStep() {
        return this.step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getStepText() {
        return this.stepText;
    }

    public void setStepText(String stepText) {
        this.stepText = stepText;
    }

    public int getWorkOrderIndex() {
        return this.workOrderIndex;
    }

    public void setWorkOrderIndex(int workOrderIndex) {
        this.workOrderIndex = workOrderIndex;
    }
}
