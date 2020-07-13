package com.yimao.cloud.order.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zhangbaobao
 * @desc 支付账号配置
 * @date 2019/9/19 9:57
 */
@Table(name = "finance_pay_account")
@Getter
@Setter
public class PayAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //公司编号
    private Integer companyId;
    //支付平台（和PayPlatform枚举类对应）：1-微信；2-支付宝；3-银行；
    private Integer platform;
    //客户端（和SystemType枚举类对应）：1-健康e家公众号；4-经销商APP；5-安装工APP；6-水机PAD；10-H5页面；
    private Integer clientType;
    //款项收取类型（默认1）：1-商品费用；2-经销代理费用
    private Integer receiveType;
    //appid
    private String appid;
    //账号详情（JSON数据）
    private String accountDetail;
    //账号用途描述
    private String description;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

}
