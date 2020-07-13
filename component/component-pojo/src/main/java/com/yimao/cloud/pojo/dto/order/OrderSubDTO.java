package com.yimao.cloud.pojo.dto.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 子订单
 */
@Getter
@Setter
@ApiModel(description = "子订单")
public class OrderSubDTO implements Serializable {

    private static final long serialVersionUID = -7540476841448566358L;

    @ApiModelProperty(position = 1, value = "订单号")
    @JsonSerialize(using=ToStringSerializer.class)
    private Long id;
    @ApiModelProperty(position = 2, value = "主订单号")
    @JsonSerialize(using=ToStringSerializer.class)
    private Long mainOrderId;

    @ApiModelProperty(position = 3, value = "状态：0-待付款；1-待审核；2-待发货；3-待出库；4-待收货；5-交易成功；6-售后中；7-交易关闭；8-已取消；")
    private Integer status;
    @ApiModelProperty(position = 4, value = "子状态(售后中状态):0-待审核(经销商)；1-待审核(总部)；2-待退货入库；3-待退款（财务）；4-售后失败；5-售后成功；")
    private Integer subStatus;
    @ApiModelProperty(position = 5, value = "是否支付")
    private Boolean pay;
    @ApiModelProperty(position = 6, value = "支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败")
    private Integer payStatus;
    @ApiModelProperty(position = 7, value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;
    @ApiModelProperty(position = 8, value = "支付端：1-立即支付；2-货到付款；")
    private Integer payTerminal;
    @ApiModelProperty(position = 8, value = "线下支付凭证")
    private String payCredential;
    @ApiModelProperty(position = 8, value = "支付凭证提交时间")
    private Date payCredentialSubmitTime;
    @ApiModelProperty(position = 9, value = "是否他人代付：0、否；1、是")
    private Boolean isReplacePay;
    @ApiModelProperty(position = 9, value = "支付时间", example = "2018-12-28 11:00:00")
    private Date payTime;
    @ApiModelProperty(position = 10, value = "订单支付单号，第三方支付流水号")
    private String tradeNo;
    @ApiModelProperty(position = 11, value = "订单金额")
    private BigDecimal fee;
    @ApiModelProperty(position = 12, value = "订单运费金额")
    private BigDecimal logisticsFee;
    @ApiModelProperty(position = 13, value = "产品ID")
    private Integer productId;
    @ApiModelProperty(position = 14, value = "产品价格", required = true)
    private BigDecimal productPrice;
    @ApiModelProperty(position = 15, required = true, value = "商品类型（大类）:1-实物商品；2-电子卡券；3-租赁商品；对应product表的mode字段")
    private Integer productType;
    @ApiModelProperty(position = 16, value = "产品型号")
    private String productModel;
    @ApiModelProperty(position = 17, value = "产品数量")
    private Integer count;
    @ApiModelProperty(position = 18, value = "订单来源：1-终端app；2-微信公众号；3-翼猫APP；4-小程序；")
    private Integer terminal;
    @ApiModelProperty(position = 19, value = "订单总价")
    private BigDecimal orderAmountFee;
    @ApiModelProperty(position = 25, value = "商品总价")
    private BigDecimal productAmountFee;

    @ApiModelProperty(position = 26, value = "平台工单号")
    private String refer;

    @ApiModelProperty(position = 20, value = "产品活动类型：1 普通产品， 2 折机商品，3-180产品  5-限时抢购；")
    private Integer activityType;
    @ApiModelProperty(position = 21, value = "订单审核时间", example = "2018-12-28 11:00:00")
    private Date auditTime;
    @ApiModelProperty(position = 22, value = "退款时间", example = "2018-12-28 11:00:00")
    private Date refundTime;
    @ApiModelProperty(position = 23, value = "分销商是否享受收益")
    private Integer userSaleFlag;
    @ApiModelProperty(position = 24, value = "订单是否已查看")
    private String isLook;

    @ApiModelProperty(position = 28, value = "取消订单时间")
    private Date cancelTime;
    @ApiModelProperty(position = 29, value = "取消订单原因")
    private String cancelReason;

    @ApiModelProperty(position = 30, value = "收货地址ID")
    private Integer addressId;
    @ApiModelProperty(position = 31, value = "下单用户ID")
    private Integer userId;
    @ApiModelProperty(position = 31, value = "会员用户ID")
    private Integer vipUserId;
    @ApiModelProperty(position = 32, value = "经销商ID")
    private Integer distributorId;
    @ApiModelProperty(position = 33, value = "安装工ID")
    private Integer engineerId;
    @ApiModelProperty(position = 34, value = "服务站ID")
    private Integer stationId;
    @ApiModelProperty(position = 35, value = "服务站区县公司ID")
    private Integer stationCompanyId;

    @ApiModelProperty(position = 38, value = "是否删除")
    private Boolean deleted;
    @ApiModelProperty(position = 39, value = "备注")
    private String remark;

    @ApiModelProperty(position = 40, value = "发货时间", example = "2018-12-28 11:00:00")
    private Date deliveryTime;
    @ApiModelProperty(position = 41, value = "收货时间", example = "2018-12-28 11:00:00")
    private Date receiveTime;
    @ApiModelProperty(position = 42, value = "订单完成时间", example = "2018-12-28 11:00:00")
    private Date completeTime;
    @ApiModelProperty(position = 43, value = "订单创建时间", example = "2018-12-28 11:00:00")
    private Date createTime;
    @ApiModelProperty(position = 44, value = "订单更新时间", example = "2018-12-28 11:00:00")
    private Date updateTime;

    //水机详细信息
    @ApiModelProperty(position = 60, value = "计费方式ID")
    private Integer costId;
    @ApiModelProperty(position = 61, value = "计费方式名称")
    private String costName;
    @ApiModelProperty(position = 62, value = "派单方式：1-手动派单；2-自动派单；")
    private Integer dispatchType;
    @ApiModelProperty(position = 63, value = "开户费，默认0")
    private BigDecimal openAccountFee;
    @ApiModelProperty(position = 64, value = "水机预约安装服务时间", example = "2018-12-28 11:00:00")
    private Date serviceTime;

    //产品详细信息
    @ApiModelProperty(position = 71, value = "产品名称")
    private String productName;
    @ApiModelProperty(position = 72, value = "产品封面图片")
    private String productImg;
    @ApiModelProperty(position = 73, value = "产品公司ID")
    private Integer productCompanyId;
    @ApiModelProperty(position = 74, value = "产品公司名称")
    private String productCompanyName;
    @ApiModelProperty(position = 75, value = "产品类目ID（三级类目）")
    private Integer productCategoryId;
    @ApiModelProperty(position = 76, value = "产品类目名称（三级类目）")
    private String productCategoryName;

    @ApiModelProperty(position = 77, value = "产品类目ID（一级类目）")
    private Integer productOneCategoryId;
    @ApiModelProperty(position = 78, value = "产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品")
    private String supplyCode;

    @ApiModelProperty(position = 78, value = "货运方式：1-包邮  2-货到付款 null-包邮")
    private Integer transportType;

    @ApiModelProperty(position = 78, value = "产品类目名称")
    private String productOneCategoryName;

    @ApiModelProperty(value = "产品类目ID（二级类目）")
    private Integer productTwoCategoryId;
    @ApiModelProperty(value = "产品类目名称（二级类目）")
    private String productTwoCategoryName;

    @ApiModelProperty(position = 79, value = "产品类目（三级类目）")
    private List<ProductCategoryDTO> productCategorys;

    //收货人详细信息
    @ApiModelProperty(position = 81, value = "收货人")
    private String addresseeName;
    @ApiModelProperty(position = 81, value = "收货人性别 1-男 2-女")
    private Integer addresseeSex;
    @ApiModelProperty(position = 82, value = "收货人手机号")
    private String addresseePhone;
    @ApiModelProperty(position = 83, value = "收货人省")
    private String addresseeProvince;
    @ApiModelProperty(position = 84, value = "收货人市")
    private String addresseeCity;
    @ApiModelProperty(position = 85, value = "收货人区")
    private String addresseeRegion;
    @ApiModelProperty(position = 86, value = "收货人详细地址")
    private String addresseeStreet;
    @ApiModelProperty(position = 87, value = "收货公司地址")
    private String addresseeCompanyName;

    //下单用户详细信息
    @ApiModelProperty(position = 91, value = "下单用户等级：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商")
    private Integer userType;
    @ApiModelProperty(position = 92, value = "下单用户等级名称")
    private String userTypeName;
    @ApiModelProperty(position = 93, value = "下单用户姓名")
    private String userName;
    @ApiModelProperty(position = 94, value = "下单用户手机号")
    private String userPhone;

    //下单用户详细信息
    @ApiModelProperty(position = 95, value = "下单用户等级：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商")
    private Integer vipUserType;
    @ApiModelProperty(position = 96, value = "下单用户等级名称")
    private String vipUserTypeName;
    @ApiModelProperty(position = 97, value = "下单用户姓名")
    private String vipUserName;
    @ApiModelProperty(position = 98, value = "下单用户手机号")
    private String vipUserPhone;

    //经销商详细信息
    @ApiModelProperty(position = 101, value = "经销商e家号")
    private Integer distributorNumber;
    @ApiModelProperty(position = 101, value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(position = 102, value = "经销商姓名")
    private String distributorName;
    @ApiModelProperty(position = 103, value = "经销商身份：1-代理商,2-经销商, 3-经销商+代理商")
    private Integer distributorType;
    @ApiModelProperty(position = 104, value = "经销商身份名称")
    private String distributorTypeName;
    @ApiModelProperty(position = 105, value = "经销商电话")
    private String distributorPhone;
    @ApiModelProperty(position = 106, value = "经销商所属省")
    private String distributorProvince;
    @ApiModelProperty(position = 107, value = "经销商所属市")
    private String distributorCity;
    @ApiModelProperty(position = 108, value = "经销商所属区")
    private String distributorRegion;

    @ApiModelProperty(position = 108, value = "经销商子账号ID")
    private Integer subDistributorId;
    @ApiModelProperty(position = 108, value = "经销商子账号")
    private String subDistributorAccount;
    @ApiModelProperty(position = 108, value = "经销商子账号姓名")
    private String subDistributorName;
    @ApiModelProperty(position = 108, value = "经销商子账号手机号")
    private String subDistributorPhone;

    @ApiModelProperty(position = 108, value = "推荐人ID")
    private Integer recommendId;
    @ApiModelProperty(position = 108, value = "推荐人账号")
    private String recommendAccount;
    @ApiModelProperty(position = 108, value = "推荐人姓名")
    private String recommendName;
    @ApiModelProperty(position = 108, value = "推荐人手机号")
    private String recommendPhone;
    @ApiModelProperty(position = 108, value = "推荐人所属省")
    private String recommendProvince;
    @ApiModelProperty(position = 108, value = "推荐人所属市")
    private String recommendCity;
    @ApiModelProperty(position = 108, value = "推荐人所属区")
    private String recommendRegion;

    //安装工详细信息
    @ApiModelProperty(position = 109, value = "安装工姓名(对应mongodb)")
    private String mongoEngineerId;
    @ApiModelProperty(position = 110, value = "安装工姓名")
    private String engineerName;
    @ApiModelProperty(position = 111, value = "安装工身份证")
    private String engineerIdCard;
    @ApiModelProperty(position = 112, value = "安装工电话")
    private String engineerPhone;
    @ApiModelProperty(position = 113, value = "安装工所属省")
    private String engineerProvince;
    @ApiModelProperty(position = 114, value = "安装工所属市")
    private String engineerCity;
    @ApiModelProperty(position = 115, value = "安装工所属区")
    private String engineerRegion;
    @ApiModelProperty(position = 116, value = "接单状态")
    private Integer workStatus;


    //服务站详细信息
    @ApiModelProperty(position = 121, value = "服务站名称")
    private String stationName;
    @ApiModelProperty(position = 122, value = "服务站联系电话")
    private String stationPhone;
    @ApiModelProperty(position = 123, value = "服务站所属省")
    private String stationProvince;
    @ApiModelProperty(position = 124, value = "服务站所属市")
    private String stationCity;
    @ApiModelProperty(position = 125, value = "服务站所属区")
    private String stationRegion;
    @ApiModelProperty(position = 126, value = "服务站区县公司名称(财务需要）")
    private String stationCompanyName;
    @ApiModelProperty(position = 127, value = "服务站区县公司所属省")
    private String stationCompanyProvince;
    @ApiModelProperty(position = 128, value = "服务站区县公司所属市")
    private String stationCompanyCity;
    @ApiModelProperty(position = 129, value = "服务站区县公司所属区")
    private String stationCompanyRegion;

    @ApiModelProperty(position = 130, value = "销售主体名称")
    private String salesSubjectName;
    @ApiModelProperty(position = 130, value = "销售主体公司名称")
    private String salesSubjectCompanyName;
    @ApiModelProperty(position = 130, value = "销售主体省")
    private String salesSubjectProvince;
    @ApiModelProperty(position = 130, value = "销售主体市")
    private String salesSubjectCity;
    @ApiModelProperty(position = 130, value = "销售主体区")
    private String salesSubjectRegion;

    @ApiModelProperty(position = 135, value = "订单详情状态:1、待付款，2、代付中，3、支付审核失败，4、支付审核中，5、待接单，6、待发货，7、订单取消中，8、出库中，9、已接单，10、待收货，11、已完成，12、待总部审核，13、待经销商审核，14、待退货，15、待退款，16、已退款，17、已取消，18、取消失败")
    private Integer detailStatus;

    @ApiModelProperty(position = 136, value = "订单详情状态描述")
    private String detailStatusDescribe;

    @ApiModelProperty(position = 136, value = "付款说明")
    private String paymentInstructions;

    @ApiModelProperty(position = 137, value = "支付审核失败的原因")
    private String payAuditFailureReason;

    //配送信息
    @ApiModelProperty(value = "物流单号")
    private String logisticsNo;
    @ApiModelProperty(value = "物流公司")
    private String logisticsCompany;

    @ApiModelProperty(position = 161, value = "会员用户是否享受收益")
    private Boolean vipUserHasIncome;

    @ApiModelProperty(value = "售后单号")
    private Long afterSalesOrderId;

    @ApiModelProperty(value = "退款申请开始时间")
    private Date cancelStartTime;

    @ApiModelProperty(value = "退款申请结束时间")
    private Date cancelEndTime;

    @ApiModelProperty(value = "支付类型 1-线上，2-线下")
    private Integer typePay;

    @ApiModelProperty(value = "审核时间")
    private Integer reviewTime;

    @ApiModelProperty(value = "查询 1-线上、线下 2-退款记录")
    private Integer queryType;

    @ApiModelProperty(value = "手续费")
    private BigDecimal formalitiesFee;
    @ApiModelProperty(value = "可退金额")
    private BigDecimal realRefundFee;//可退金额
    @ApiModelProperty(value = "应退金额")
    private BigDecimal refundFee;//应退金额
    @ApiModelProperty(value = "退款审核时间")
    private Date refundFinanceTime;//退款审核时间
    @ApiModelProperty(value = "退款审核人")
    private String financer;

}
