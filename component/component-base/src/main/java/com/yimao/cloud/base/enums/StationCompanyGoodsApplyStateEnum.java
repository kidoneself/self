package com.yimao.cloud.base.enums;


/**
 * 服务站公司物资申请状态
 *
 * @author Liu Long Jie
 * @date 2020-6-18 14:12:14
 */
public enum StationCompanyGoodsApplyStateEnum {

    /**
     * 申请状态 1-待初审 2-待复核 3-发货中 4-已完成 5-打回 0-已取消
     */
    ALREADY_CANCEL("已取消", 0),
    WAITING_AUDIT("待审核", 1),
    WAITING_RE_AUDIT("待复核", 2),
    WAITING_SHIPMENTS("待发货", 3),
    SHIPMENTS("配送中", 4),
    COMPLETED("已完成", 5),
    REPULSE("打回", 6);


    public final String name;
    public final int value;

    StationCompanyGoodsApplyStateEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
