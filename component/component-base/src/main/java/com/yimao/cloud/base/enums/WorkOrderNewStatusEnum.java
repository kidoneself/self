package com.yimao.cloud.base.enums;

import java.util.Arrays;

/****
 * 新安装工单状态
 */
public enum WorkOrderNewStatusEnum {
	WAIT_INSTALL("待安装", 1), PROCESSING("处理中", 2), PENDING("挂单", 3), COMPLETED("已完成", 4);

	public final String name;
	public final int value;

	WorkOrderNewStatusEnum(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * 根据工单状态判断是否接单
	 */
	public static boolean accepted(int status) {
		return status == PENDING.value || status == PROCESSING.value || status == COMPLETED.value;
	}

	/**
	 * 根据工单状态判断是否完成
	 */
	public static boolean completed(int status) {
		return status == COMPLETED.value;
	}

	public static WorkOrderNewStatusEnum find(Integer value) {
		if (value == null) {
			return null;
		}
		return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
	}

	/**
	 * 判断状态是否正确
	 */
	public static boolean existsStatus(int status) {
		return status == PENDING.value || status == PROCESSING.value || status == COMPLETED.value
				|| status == WAIT_INSTALL.value;
	}
}
