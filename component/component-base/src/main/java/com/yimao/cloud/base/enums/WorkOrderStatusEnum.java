package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 描述：工单状态
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
public enum WorkOrderStatusEnum {

    // REVIEW_FAILURE("审核未通过", -2),
    // WAITING_AUDIT("审核中", -1),
    // PAID("已支付", 0),
    // ASSIGNED("工单已分配待接单（包含手动分配和系统分配）", 1),
    // ACCEPTED("安装工接单", 2),
    // INSTALLING("安装中", 3),
    // COMPLETED("安装完成", 4),
    // NEED_PAY("待付款", 5),
    // REFUSE("拒单", 6);

    ASSIGNED("未受理", 1),
    ACCEPTED("已受理", 2),
    INSTALLING("处理中", 3),
    COMPLETED("已完成", 4);

    public final String name;
    public final int value;

    WorkOrderStatusEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据工单状态判断是否接单
     */
    public static boolean accepted(int status) {
        return status == ACCEPTED.value || status == INSTALLING.value || status == COMPLETED.value;
    }

    /**
     * 根据工单状态判断是否完成
     */
    public static boolean completed(int status) {
        return status == COMPLETED.value;
    }

    public static WorkOrderStatusEnum find(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

    // /**
    //  * 获取云平台对应的工单状态（提供给安装工APP用）
    //  * 老系统工单状态（1 未受理,2 已受理,3 处理中,4 已完成）
    //  *
    //  * @param status 新系统的工单状态
    //  */
    // public static int getOldState(int status) {
    //     if (status == ASSIGNED.value || status == NEED_PAY.value || status == WAITING_AUDIT.value || status == REVIEW_FAILURE.value || status == PAID.value) {
    //         return 1;
    //     }
    //     if (status == ACCEPTED.value) {
    //         return 2;
    //     }
    //     if (status == INSTALLING.value) {
    //         return 3;
    //     }
    //     if (status == COMPLETED.value) {
    //         return 4;
    //     }
    //     return 1;
    // }
}
