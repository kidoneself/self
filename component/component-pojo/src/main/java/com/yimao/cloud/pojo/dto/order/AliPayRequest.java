package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：支付宝支付下单请求参数
 *
 * @Author Zhang Bo
 * @Date 2019/9/26
 */
@ApiModel(description = "支付宝支付下单请求参数")
@Getter
@Setter
public class AliPayRequest {

    @ApiModelProperty(position = 1, value = "商户网站唯一订单号")
    private String outTradeNo;
    @ApiModelProperty(position = 2, value = "订单总金额，单位为元，精确到小数点后两位")
    private Double totalAmount;
    @ApiModelProperty(position = 3, value = "商品名称。")
    private String body;
    @ApiModelProperty(position = 4, value = "商品的标题/交易标题/订单标题/订单关键字等。", hidden = true)
    private String subject;
    @ApiModelProperty(position = 5, value = "支付宝服务器主动通知商户服务器里指定的页面http/https路径。", hidden = true)
    private String notifyUrl;
    @ApiModelProperty(position = 6, value = "1-商品订单 2-经销商订单 3-续费订单 4-工单", hidden = true)
    private Integer orderType;

    @ApiModelProperty(position = 20, value = "公司编号", hidden = true)
    private Integer companyId;
    @ApiModelProperty(position = 20, value = "支付平台：1-微信；2-支付宝；3-银行；", hidden = true)
    private Integer platform;
    @ApiModelProperty(position = 20, value = "客户端：1-健康e家公众号；4-经销商APP；5-安装工APP；6-水机PAD；10-H5页面；", hidden = true)
    private Integer clientType;
    @ApiModelProperty(position = 20, value = "款项收取类型（默认1）：1-商品费用；2-经销代理费用", hidden = true)
    private Integer receiveType;

}
