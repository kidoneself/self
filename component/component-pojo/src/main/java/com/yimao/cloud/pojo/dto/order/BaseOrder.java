package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 下单时所需要的订单信息
 *
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@Getter
@Setter
@ApiModel(description = "订单基础对象")
public class BaseOrder implements Serializable {

    private static final long serialVersionUID = 279335909704339112L;

    @ApiModelProperty(position = 1, required = true, value = "终端：2-微信公众号；3-翼猫APP；4-小程序；6-H5页面下单")
    private Integer terminal;
    @ApiModelProperty(position = 2, value = "支付方式：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;
    @ApiModelProperty(position = 3, required = true, value = "购买数量")
    private Integer count;
    @ApiModelProperty(position = 4, value = "商品总金额")
    private BigDecimal productAmountFee;
    @ApiModelProperty(position = 5, value = "订单总金额")
    private BigDecimal orderAmountFee;
    @ApiModelProperty(position = 6, value = "订单备注")
    private String remark;
    @ApiModelProperty(position = 7, value = "收货地址ID")
    private Integer addressId;
    @ApiModelProperty(position = 8, value = "产品ID（非购物车下单时必传）")
    private Integer productId;
    @ApiModelProperty(position = 8, value = "产品名称")
    private String productName;
    @ApiModelProperty(position = 9, value = "用户选择的水机安装时间（非购物车下单时必传）")
    private Date serviceTime;
    @ApiModelProperty(position = 10, value = "水机计费方式ID（非购物车下单时必传）")
    private Integer costId;
    @ApiModelProperty(position = 10, value = "水机计费方式名称")
    private String costName;
    @ApiModelProperty(position = 11, value = "派单方式：1-手动派单；2-自动派单（水机产品必传）")
    private Integer dispatchType;
    @ApiModelProperty(position = 12, value = "安装工ID（非购物车下单时必传）")
    private Integer engineerId;
    @ApiModelProperty(position = 13, value = "水机安装开户费")
    private BigDecimal openAccountFee;
    @ApiModelProperty(position = 14, value = "支付类型：1-立即支付；2-货到付款")
    private Integer payTerminal;
    @ApiModelProperty(position = 15, value = "商品类型(大类):1-实物商品；2-电子卡券；3-租赁商品")
    private Integer mode;
    @ApiModelProperty(position = 16, value = "商品市场价")
    private BigDecimal productMarketFee;
    @ApiModelProperty(position = 17, value = "产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；")
    private String supplyCode;
    @ApiModelProperty(position = 18, value = "运费")
    private BigDecimal logisticsFee;
    @ApiModelProperty(position = 19, value = "是否他人代付：0、否；1、是")
    private Boolean isReplacePay;

    @ApiModelProperty(position = 20, value = "商品所参与的活动ID集合")
    private List<Integer> productActivityIdList;
}
