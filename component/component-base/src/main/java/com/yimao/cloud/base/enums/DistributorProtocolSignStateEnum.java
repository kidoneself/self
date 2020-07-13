package com.yimao.cloud.base.enums;


/**
 * 经销商h合同签署状态
 * @author Liu long jie
 * @date 2019/10/30
 */
public enum DistributorProtocolSignStateEnum {

    /**
     * 状态 0-未签署、1-已签署
     */
    NOT_SIGN("未签署", 0),
    SIGN("已签署", 1);


    public final String name;
    public final int value;

    DistributorProtocolSignStateEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
