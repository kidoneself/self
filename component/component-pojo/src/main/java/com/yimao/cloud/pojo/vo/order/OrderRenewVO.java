package com.yimao.cloud.pojo.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 续费订单VO对象，前端展示用
 **/
@ApiModel(description = "续费订单VO对象，前端展示用")
@Getter
@Setter
public class OrderRenewVO {

    @ApiModelProperty(value = "续费单号")
    private String id;
    @ApiModelProperty(value = "工单号")
    private String workOrderId;
    @ApiModelProperty(value = "原始主订单号")
    private String mainOrderId;
    @ApiModelProperty(value = "原始订单号")
    private String orderId;
    @ApiModelProperty(value = "订单支付单号，第三方支付流水号")
    private String tradeNo;
    @ApiModelProperty(value = "费用金额")
    private BigDecimal amountFee;
    @ApiModelProperty(value = "水机用户姓名")
    private String waterUserName;
    @ApiModelProperty(value = "水机用户手机号")
    private String waterUserPhone;
    @ApiModelProperty(value = "第几次续费")
    private Integer times;
    @ApiModelProperty(value = "产品id")
    private Integer productId;
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "产品一级类目id")
    private Integer productFirstCategoryId;
    @ApiModelProperty(value = "产品一级类目名称")
    private String productFirstCategoryName;
    @ApiModelProperty(value = "产品二级类目id")
    private Integer productSecondCategoryId;
    @ApiModelProperty(value = "产品二级类目名称")
    private String productSecondCategoryName;
    @ApiModelProperty(value = "产品三级类目id")
    private Integer productCategoryId;
    @ApiModelProperty(value = "产品三级类目名称")
    private String productCategoryName;
    @ApiModelProperty(value = "产品公司id")
    private Integer productCompanyId;
    @ApiModelProperty(value = "产品公司名称")
    private String productCompanyName;
    @ApiModelProperty(value = "计费模板id")
    private Integer costId;
    @ApiModelProperty(value = "计费方式：1-按流量计费；2-按时间计费；")
    private Integer costType;
    @ApiModelProperty(value = "计费方式：按流量计费；按时间计费；")
    private String costTypeName;
    @ApiModelProperty(value = "计费模板名称")
    private String costName;
    @ApiModelProperty(value = "上一次计费模板id")
    private Integer lastCostId;
    @ApiModelProperty(value = "上一次计费方式")
    private Integer lastCostCode;
    @ApiModelProperty(value = "上一次计费模板名称")
    private String lastCostName;
    @ApiModelProperty(value = "经销商ID")
    private Integer distributorId;
    @ApiModelProperty(value = "经销商类型")
    private Integer distributorType;
    @ApiModelProperty(value = "经销商类型名称")
    private String distributorTypeName;
    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(value = "经销商名称")
    private String distributorName;
    @ApiModelProperty(value = "经销商所在省")
    private String distributorProvince;
    @ApiModelProperty(value = "经销商所在市")
    private String distributorCity;
    @ApiModelProperty(value = "经销商所在区")
    private String distributorRegion;
    @ApiModelProperty(value = "安装工姓名")
    private String engineerName;
    @ApiModelProperty(value = "安装工联系方式")
    private String engineerPhone;
    @ApiModelProperty(value = "安装工服务站")
    private String engineerStationName;
    @ApiModelProperty(value = "设备所在地址")
    private String deviceAddress;
    @ApiModelProperty(value = "设备型号")
    private String deviceModel;
    @ApiModelProperty(value = "设备安装时间")
    private Date deviceInstallationTime;
    @ApiModelProperty(value = "设备SN码")
    private String snCode;
    @ApiModelProperty(value = "是否支付")
    private Boolean pay;
    @ApiModelProperty(value = "支付时间")
    private Date payTime;
    @ApiModelProperty(value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;
    @ApiModelProperty(value = "支付类型名称：微信；支付宝；POS机；转账；")
    private String payTypeName;
    @ApiModelProperty(value = "支付状态：1-待审核，2-支付成功，3-支付失败")
    private Integer status;
    @ApiModelProperty(value = "支付状态名称：待审核，支付成功，支付失败")
    private String statusName;
    @ApiModelProperty(value = "订单创建时间")
    private Date createTime;
    @ApiModelProperty(value = "云平台订单号")
    private String workOrder;
    @ApiModelProperty(value = "订单来源：8、广告屏  9、总部业务系统")
    private Integer terminal;
    @ApiModelProperty(value = "订单来源：8、广告屏  9、总部业务系统")
    private String terminalName;
    @ApiModelProperty(value = "线下支付凭证")
    private String payCredential;
    @ApiModelProperty(value = "线下支付凭证")
    private String payCredentialSubmitTime;
    @ApiModelProperty(value = "续费工单审核失败原因")
    private String reason;
    @ApiModelProperty(value = "是否删除")
    private Boolean deleted;
    @ApiModelProperty(value = "附件")
    private String attach;

    @ApiModelProperty(value = "设备所在省")
    private String province;
    @ApiModelProperty(value = "设备所在市")
    private String city;
    @ApiModelProperty(value = "设备所在区")
    private String region;

}
