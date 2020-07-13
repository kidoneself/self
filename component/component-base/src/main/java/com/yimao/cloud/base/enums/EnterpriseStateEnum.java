package com.yimao.cloud.base.enums;

/**
 * 企业审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核
 *
 * @author Zhang Bo
 * @date 2018/10/23.
 */
public enum EnterpriseStateEnum {

    /**
     *
     * 企业审核状态/0-未审核、1-审核通过、2-审核不通过、3-无需审核
     */
    UN_AUDIT("未审核", 0),
    PASS_AUDIT("审核通过", 1),
    UN_PASS_AUDIT("审核不通过", 2),
    NO_NEED_AUDIT("无需审核", 3);

    public final String name;
    public final int value;

    EnterpriseStateEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
