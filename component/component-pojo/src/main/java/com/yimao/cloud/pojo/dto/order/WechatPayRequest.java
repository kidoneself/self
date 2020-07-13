package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：微信支付下单请求参数
 *
 * @Author Zhang Bo
 * @Date 2019/9/26
 */
@ApiModel(description = "微信支付下单请求参数")
@Getter
@Setter
public class WechatPayRequest {

    @ApiModelProperty(position = 1, value = "商户（翼猫）的支付单号（主订单号）")
    private String out_trade_no;
    @ApiModelProperty(position = 2, value = "订单总金额")
    private Double total_fee;
    @ApiModelProperty(position = 3, value = "openId")
    private String openId;

    @ApiModelProperty(position = 11, value = "商品描述", hidden = true)
    private String body;
    @ApiModelProperty(position = 12, value = "支付类型 JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付,MWEB--H5支付", hidden = true)
    private String tradeType;
    @ApiModelProperty(position = 13, value = "异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。", hidden = true)
    private String notifyUrl;
    @ApiModelProperty(position = 14, value = "终端IP（用户）", hidden = true)
    private String spbillCreateIp;
    @ApiModelProperty(position = 15, value = "1-商品订单 2-经销商订单 3-续费订单 4-工单", hidden = true)
    private Integer orderType;

    @ApiModelProperty(position = 20, value = "公司编号", hidden = true)
    private Integer companyId;
    @ApiModelProperty(position = 21, value = "支付平台：1-微信；2-支付宝；3-银行；", hidden = true)
    private Integer platform;
    @ApiModelProperty(position = 22, value = "客户端：1-健康e家公众号；4-经销商APP；5-安装工APP；6-水机PAD；10-H5页面；", hidden = true)
    private Integer clientType;
    @ApiModelProperty(position = 23, value = "款项收取类型（默认1）：1-商品费用；2-经销代理费用", hidden = true)
    private Integer receiveType;

}
