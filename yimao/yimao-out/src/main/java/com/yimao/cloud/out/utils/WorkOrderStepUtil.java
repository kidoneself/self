package com.yimao.cloud.out.utils;

import com.yimao.cloud.base.enums.WorkOrderStateEnum;
import com.yimao.cloud.base.enums.WorkOrderStepEnum;
import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.RepairWorkOrderDTO;

/**
 * @ClassName MaintenanceWorkOrderStepUtil
 * @Description
 * @Author liu yi
 * @Date 2019/7/13 17:12
 * @Version 1.0
 **/
public class WorkOrderStepUtil {
    public static void setMaintenanceOrderStep(MaintenanceWorkOrderDTO dto, WorkOrderTypeEnum workOrderType, int nextStep) {
        WorkOrderStepEnum nextStepEnum = WorkOrderStepEnum.getStepByBaideNextStep(nextStep, Integer.parseInt(workOrderType.getType()));
        dto.setNextStep(nextStep);
        switch (nextStepEnum) {
            case WORK_ORDER_STEP_END:
                dto.setState(WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.state);
                dto.setStateText(WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.stateText);
                dto.setCurrentStep(WorkOrderStepEnum.MAINTENANCE_WORK_ORDER_STEP_FINISH_WORK.getStep());
                break;
            case MAINTENANCE_WORK_ORDER_STEP_APPOINTMENT:
                dto.setState(WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.state);
                dto.setStateText(WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.stateText);
                dto.setCurrentStep(WorkOrderStepEnum.MAINTENANCE_WORK_ORDER_STEP_ACCEPT.getStep());
                break;
            case MAINTENANCE_WORK_ORDER_STEP_SCANBATCHCODE:
                dto.setState(WorkOrderStateEnum.WORKORDER_STATE_SERVING.state);
                dto.setStateText(WorkOrderStateEnum.WORKORDER_STATE_SERVING.stateText);
                dto.setCurrentStep(WorkOrderStepEnum.MAINTENANCE_WORK_ORDER_STEP_STARTSERVER.getStep());
                break;
            default:
                int currentStep = nextStepEnum.getStep() - 1;
                WorkOrderStepEnum currentStepEnum = WorkOrderStepEnum.getStepByBaideNextStep(currentStep, Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_MAINTENANCE.getType()));
                dto.setCurrentStep(currentStepEnum.getStep());
        }
    }

    public static void setRepairWorkOrderStep(RepairWorkOrderDTO repairOrder, WorkOrderTypeEnum workOrderType, int nextStep) {
        WorkOrderStepEnum nextStepEnum = WorkOrderStepEnum.getStepByBaideNextStep(nextStep, Integer.parseInt(workOrderType.getType()));
        repairOrder.setNextStep(nextStep);
        switch(nextStepEnum) {
            case REPAIR_WORK_ORDER_STEP_STARTSERVER:
                repairOrder.setState(WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.state);
                repairOrder.setStateText(WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.stateText);
                repairOrder.setCurrentStep(WorkOrderStepEnum.REPAIR_WORK_ORDER_STEP_ACCEPT.getStep());
                break;
            case REPAIR_WORK_ORDER_STEP_FACTFAULT_DESC:
                repairOrder.setState(WorkOrderStateEnum.WORKORDER_STATE_SERVING.state);
                repairOrder.setStateText(WorkOrderStateEnum.WORKORDER_STATE_SERVING.stateText);
                repairOrder.setCurrentStep(WorkOrderStepEnum.REPAIR_WORK_ORDER_STEP_STARTSERVER.getStep());
                break;
            case WORK_ORDER_STEP_END:
                repairOrder.setState(WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.state);
                repairOrder.setStateText(WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.stateText);
                repairOrder.setCurrentStep(WorkOrderStepEnum.REPAIR_WORK_ORDER_STEP_FINISH_WORK.getStep());
                break;
            default:
                int currentStep = nextStepEnum.getStep() - 1;
                WorkOrderStepEnum currentStepEnum = WorkOrderStepEnum.getStepByBaideNextStep(currentStep, Integer.parseInt(WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType()));
                repairOrder.setCurrentStep(currentStepEnum.getStep());
        }
    }
}
