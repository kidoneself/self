package com.yimao.cloud.pojo.dto.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @description 发货详情信息
 * @date 2019/1/10 16:24
 **/
@Data
@ApiModel(description = "发货详情信息")
public class DeliveryInfoDTO implements Serializable {
    private static final long serialVersionUID = 1868537931627634435L;

    @ApiModelProperty(value = "0-待付款 1-待审核 2-待发货 3-待出库  4-待收货 5-交易成功 6-售后中 7-交易关闭 8-已取消")
    private Integer status;

    //订单信息
    @ApiModelProperty(value = "订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @ApiModelProperty(value = "主订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long mainOrderId;
    @ApiModelProperty(value = "下单用户id")
    private Integer userId;
    @ApiModelProperty(value = "支付端：1-经销商支付；2-他人代付；3-用户支付；")
    private Integer payTerminal;
    @ApiModelProperty(value = "用户身份：0-体验版经销商；1-微创版经销商；2-个人版经销商；3-分享用户；4-普通用户；5-企业版经销商（主）；6-企业版经销商（子）；7-分销用户；")
    private Integer userType;
    @ApiModelProperty(value = "支付方式：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;
    @ApiModelProperty(value = "工单号")
    private Long workId;
    @ApiModelProperty( value = "发货时间",example = "2018-12-28 11:00:00")
    private Date deliveryTime;
    @ApiModelProperty( value = "支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败")
    private Integer payStatus;
    @ApiModelProperty( value = "下单时间",example = "2018-12-28 11:00:00")
    private Date createTime;           //订单创建时间
    @ApiModelProperty( value = "订单完成时间",example = "2018-12-28 11:00:00")
    private Date completeTime;         //订单完成时间
    @ApiModelProperty( value = "支付时间",example = "2018-12-28 11:00:00")
    private Date payTime;
    @ApiModelProperty( value = "订单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；")
    private Integer terminal;
    @ApiModelProperty( value = "活动方式:1 普通产品， 2 折机商品，3-180产品  type 是租赁商品是有值")
    private Integer activityType;
    @ApiModelProperty( value = "订单支付单号，第三方支付流水号")
    private String tradeNo;            //订单支付单号，第三方支付流水号


    //收货信息
    @ApiModelProperty(value = "收货人")
    private String addresseeName;
    @ApiModelProperty(value = "联系方式")
    private String addresseePhone;    //收货人手机号
    @ApiModelProperty(value = "收货人省")
    private String addresseeProvince;
    @ApiModelProperty(value = "收货人市")
    private String addresseeCity;
    @ApiModelProperty(value = "收货人区")
    private String addresseeRegion;
    @ApiModelProperty(value = "收货人街道")

    //配送信息(发货信息)
    private String addresseeStreet;
    @ApiModelProperty(value = "物流公司")
    private String logisticsCompany;            //物流公司
    @ApiModelProperty(value = "物流单号")
    private String logisticsNo;                 //物流单号
    @ApiModelProperty(value = "发货件数")
    private Integer deliveryNum;                       //发货件数
    @ApiModelProperty(value = "发货数量/盒")
    private Integer boxNum;                     //发货数量/盒
    @ApiModelProperty(value = "重量")
    private Integer weigh;                      //重量
    @ApiModelProperty(value = "备注")
    private String remark;                      //备注
    //发货联系人

    //发货地址



    //商品信息
    @ApiModelProperty(value = "产品封面图片")
    private String productImg;
    @ApiModelProperty(value = "产品id")
    private Integer productId;
    @ApiModelProperty(value = "产品类目名称")
    private String productCategoryName;
    @ApiModelProperty(value = "商品数量")
    private Integer count;
    @ApiModelProperty(value = "单价")
    private BigDecimal marketPrice;             //市场价格
    @ApiModelProperty(value = "商品总价")
    private BigDecimal productAmountFee;//商品总价

    //费用信息
    @ApiModelProperty(value = "运费")
    private BigDecimal logisticsFee;            //运费
    @ApiModelProperty(value = "总金额")
    private BigDecimal orderAmountFee;   //订单总价

    //收益归属
    @ApiModelProperty(value = "经销商ID")
    private Integer distributorId;
    @ApiModelProperty(value = "经销商身份名称")
    private String distributorTypeName;  //经销商身份名称
    @ApiModelProperty(value = "服务站名称")
    private String stationName;



    //操作记录






}
