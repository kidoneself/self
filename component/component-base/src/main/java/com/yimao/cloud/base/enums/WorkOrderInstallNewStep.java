package com.yimao.cloud.base.enums;
/***
 * 新工单步骤
 * @author zhangbaobao
 *
 */
public enum WorkOrderInstallNewStep {
	
	START(0, "开始"),
	COLLECT_WATER(1,"采集水源"),
	ACTIVATING(2, "激活"),
	PAY(3, "支付"),
    SIGN_CONTRACT(4, "签约"),
    COMPLETE_WORK_ORDER(5, "完成工单"),
	END(6,"结束");
    public int value;
    public String name;

    WorkOrderInstallNewStep(int value, String name) {
        this.value = value;
        this.name = name;
    }

}
