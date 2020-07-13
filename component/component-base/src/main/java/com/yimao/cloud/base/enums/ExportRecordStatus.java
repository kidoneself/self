package com.yimao.cloud.base.enums;

/**
 * 描述：导出记录状态类型
 *
 * @Author Zhang Bo
 * @Date 2019/11/25
 */
public enum ExportRecordStatus {

    WAITING("等待导出", 1),
    IN_EXPORT("导出中", 2),
    SUCCESSFUL("导出成功", 3),
    FAILURE("导出失败", 4);

    public final String name;
    public final int value;

    ExportRecordStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
