package com.yimao.cloud.base.enums;

/**
 * @author Lizhqiang
 * @date 2019/1/2
 */
public enum ProtocolStateEnum {

    /**
     * 合同签署状态
     * <p>
     * 0-已签署
     * 1-未签署
     * 2-无需签署
     */

    SIGNED("SIGNED", 0),
    UNSIGNED("UNSIGNED", 1),
    NOSIGN("NOSIGN", 2);

    public final String name;
    public final int value;

    ProtocolStateEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
