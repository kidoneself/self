package com.yimao.cloud.pojo.dto.order;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Zhang Bo
 * @date 2019/9/25
 */
@Getter
@Setter
public class PayAccountDetail {

    //微信、支付宝appid
    private String appid;

    //微信商户号
    private String mchId;
    //微信secret
    private String secret;
    //微信key
    private String key;

    //支付宝公钥
    private String publicKey;
    //支付宝商户私钥
    private String privateKey;

    //银行户名
    private String companyName;
    //开户行
    private String bankName;
    //银行账号
    private String bankAccount;

}
