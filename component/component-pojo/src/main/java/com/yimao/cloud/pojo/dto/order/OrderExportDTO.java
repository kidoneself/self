package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-05-15 11:58:49
 **/
@Getter
@Setter
@ApiModel(description = "订单")
public class OrderExportDTO implements Serializable {

    private static final long serialVersionUID = -1471484025825970061L;
    @ApiModelProperty(value = "主订单号")
    private Long mainOrderId;

    @ApiModelProperty(value = "订单号")
    private Long id;

    @ApiModelProperty(value = "平台工单号")
    private String refer;

    @ApiModelProperty(value = "订单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；")
    private String terminal;

    @ApiModelProperty(value = "状态：0-待付款；1-待审核；2-待发货；3-待出库；4-待收货；5-交易成功；6-售后中；7-交易关闭；8-已取消；")
    private String status;

    @ApiModelProperty(value = "下单时间", example = "2018-12-28 11:00:00")
    private String createTime;

    @ApiModelProperty(value = "支付方式：1-微信；2-支付宝；3-POS机；4-转账；")
    private String payType;

    @ApiModelProperty(value = "支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败")
    private String payStatus;

    @ApiModelProperty(value = "支付时间", example = "2018-12-28 11:00:00")
    private String payTime;

    @ApiModelProperty(value = "流水号")
    private String tradeNo;

    @ApiModelProperty(value = "订单完成时间", example = "2018-12-28 11:00:00")
    private String completeTime;

    @ApiModelProperty(value = "下单用户身份")
    private String userType;

    @ApiModelProperty(value = "下单用户ID")
    private Integer userId;

    @ApiModelProperty(value = "收货人")
    private String addresseeName;

    @ApiModelProperty(value = "联系方式")
    private String addresseePhone;

    @ApiModelProperty(value = "收货地址")
    private String addressee;

    @ApiModelProperty(value = "活动方式：1 普通产品， 2 折机商品，3-180产品  type 是租赁商品是有值；")
    private String activityType;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品公司名称")
    private String productCompanyName;

    @ApiModelProperty(value = "产品类目名称（一级类目）")
    private String productFirstCategoryName;

    @ApiModelProperty(value = "产品类目名称（二级类目）")
    private String productSecondCategoryName;

    @ApiModelProperty(value = "产品类目名称（三级类目）")
    private String productCategoryName;

    @ApiModelProperty(value = "产品数量")
    private Integer count;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal orderAmountFee;

    @ApiModelProperty(value = "经销商身份")
    private String distributorTypeName;

    @ApiModelProperty(value = "经销商姓名")
    private String distributorName;

    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;

    @ApiModelProperty(value = "经销商所属省")
    private String distributorProvince;

    @ApiModelProperty(value = "经销商所属市")
    private String distributorCity;

    @ApiModelProperty(value = "经销商所属区")
    private String distributorRegion;

    @ApiModelProperty(value = "是否有子账号")
    private String hasSubDistributor;

    @ApiModelProperty(value = "子账号")
    private String subDistributorAccount;

    @ApiModelProperty(value = "子账号姓名")
    private String subDistributorName;

    @ApiModelProperty(value = "会员用户ID")
    private Integer vipUserId;

    @ApiModelProperty(value = "会员用户是否有收益")
    private String vipUserHasIncomeTxt;

    @ApiModelProperty(value = "备注")
    private String remark;


}
