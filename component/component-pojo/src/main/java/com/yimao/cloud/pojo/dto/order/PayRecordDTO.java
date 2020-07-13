package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhilin.he
 * @description 支付记录信息
 * @date 2019/4/3 11:35
 **/
@Getter
@Setter
@ApiModel(description = "支付记录")
public class PayRecordDTO implements Serializable {

    private static final long serialVersionUID = 5020908888317774718L;


    @ApiModelProperty(position = 1, value = "支付id")
    private Integer id;
    @ApiModelProperty(position = 2, value = "主订单ID")
    private String mainOrderId;
    @ApiModelProperty(position = 4, value = "订单类型1、普通订单  2、续费订单 3、经销商订单 4、安装工单")
    private Integer orderType;
    @ApiModelProperty(position = 5, value = "openid")
    private String openid;
    @ApiModelProperty(position = 5, value = "支付用户ID")
    private Integer userId;
    @ApiModelProperty(position = 6, value = "订单支付单号，第三方支付流水号")
    private String tradeNo;
    @ApiModelProperty(position = 7, value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;
    @ApiModelProperty(position = 7, value = "JSAPI -JSAPI支付、NATIVE -Native支付、APP -APP支付")
    private String tradeType;
    @ApiModelProperty(position = 8, value = "支付端")
    private Integer terminal;
    @ApiModelProperty(position = 9, value = "总金额")
    private BigDecimal amountTotal;
    @ApiModelProperty(position = 10, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 11, value = "支付时间")
    private Date payTime;
    @ApiModelProperty(position = 12, value = "工单id")
    private String workOrderId;

    @ApiModelProperty(position = 20, value = "公司编号", hidden = true)
    private Integer companyId;
    @ApiModelProperty(position = 21, value = "支付平台：1-微信；2-支付宝；3-银行；", hidden = true)
    private Integer platform;
    @ApiModelProperty(position = 22, value = "客户端：1-健康e家公众号；4-经销商APP；5-安装工APP；6-水机PAD；10-H5页面；", hidden = true)
    private Integer clientType;
    @ApiModelProperty(position = 23, value = "款项收取类型（默认1）：1-商品费用；2-经销代理费用", hidden = true)
    private Integer receiveType;

}
