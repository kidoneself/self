package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: 子订单列表
 * @author: yu chunlei
 * @create: 2019-03-22 11:17:10
 **/
@Data
@ApiModel(description = "子订单列表")
public class OrderSubListDTO implements Serializable {

    @ApiModelProperty(position = 1, value = "主订单号")
    private Long mainOrderId;

    @ApiModelProperty(value = "订单类型：1-普通订单  2-续费订单")
    private Integer type;

    @ApiModelProperty(value = "子订单类型：1-为自己下单；2-为客户下单")
    private Integer subType;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "商品总价")
    private BigDecimal productAmountFee;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal orderAmountFee;

    @ApiModelProperty(value = "订单购买数量")
    private Integer count;

    @ApiModelProperty(value = "订单运费")
    private BigDecimal logisticsFee;

    @ApiModelProperty(value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;

    @ApiModelProperty(value = "是否支付")
    private Boolean pay;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "订单支付单号，第三方支付流水号")
    private String tradeNo;

    @ApiModelProperty(value = "线下支付凭证")
    private String payCredential;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "订单信息结果集")
    private List<OrderSubDTO> orderSubList;

    @ApiModelProperty(value = "服务人员和安装时间是否一致：0-否，1-是")
    private Integer isDifference;
}
