package com.yimao.cloud.base.enums;


/**
 * 经销商订单支付类型
 * @author Liu Yi
 * @date 2019/1/8.
 */
public enum DistributorOrderPayTypeEnum {

    /**
     *  支付类型：1-微信；2-支付宝；3-POS机；4-转账；
     */
    ALIPAY("支付宝支付", 2),
    WECHAT("微信支付", 1),
    POS("POS机刷卡", 3),
    BANK_TRANSFER("银行转账", 4);

    public final String name;
    public final int value;

    DistributorOrderPayTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
