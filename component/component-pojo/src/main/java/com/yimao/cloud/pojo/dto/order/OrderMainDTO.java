package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author zhilin.he
 * @description 主订单DTO
 * @date 2019/1/9 15:04
 **/
@Data
@ApiModel(description = "主订单DTO")
public class OrderMainDTO implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4025595150103999206L;

	@ApiModelProperty(value = "主订单号")
    @JsonSerialize(using=ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "订单类型：1-普通订单  2-续费订单")
    private Integer type;

    @ApiModelProperty(value = "子订单类型：1-为自己下单；2-为客户下单")
    private Integer subType;
    
    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "商品总价")
    //private BigDecimal productTotalFee;
    private BigDecimal productAmountFee;//商品总价

    @ApiModelProperty(value = "订单总金额")
//    private BigDecimal orderTotalFee;
    private BigDecimal orderAmountFee;//订单总金额

    @ApiModelProperty(value = "订单购买数量")
    private Integer count;

    @ApiModelProperty(value = "订单运费")
    private BigDecimal logisticsFee;

    @ApiModelProperty(value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;

    @ApiModelProperty(value = "是否支付")
    private Boolean pay;
    
    @ApiModelProperty(value = "支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败")
    private Integer payStatus;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "订单支付单号，第三方支付流水号")
    private String tradeNo;

    @ApiModelProperty(value = "线下支付凭证")
    private String payCredential;

    @ApiModelProperty(value = "支付凭证提交时间")
    private Date payCredentialSubmitTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否他人代付：0、否；1、是")
    private Boolean isReplacePay;

    @ApiModelProperty(value = "是否删除")
    private Boolean deleted;
    
    @ApiModelProperty(value = "产品公司")
    private Integer productCompanyId;
    
    @ApiModelProperty(value = "提交凭证开始时间")
    private Date startCredentialSubmitTime;
    @ApiModelProperty(value = "提交凭证结束时间")
    private Date endCredentialSubmitTime;
    @ApiModelProperty(value = "支付类型：1-立即支付；2-货到付款")
    private Integer payTerminal;
    @ApiModelProperty(value = "产品公司名字")
    private String productCompanyName;
    @ApiModelProperty(value = "下单用户手机")
    private String userPhone;
}
