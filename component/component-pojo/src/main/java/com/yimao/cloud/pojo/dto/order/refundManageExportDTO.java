package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019-10-23
 */
@Data
@ApiModel(description = "退款管理导出类")
public class refundManageExportDTO {


    @ApiModelProperty(position = 1, value = "订单号")
    private Long id;
    @ApiModelProperty(position = 2, value = "主订单号")
    private Long mainOrderId;
    @ApiModelProperty(value = "售后单号")
    private Long afterSalesOrderId;
    @ApiModelProperty(position = 18, value = "订单来源：1-终端app；2-微信公众号；3-翼猫APP；4-小程序；")
    private String terminal;
    @ApiModelProperty(position = 78, value = "产品类目名称（一级类目）")
    private String productOneCategoryName;
    @ApiModelProperty(position = 76, value = "产品类目名称（三级类目）")
    private String productCategoryName;
    @ApiModelProperty(value = "产品类目名称（二级类目）")
    private String productTwoCategoryName;
    @ApiModelProperty(position = 7, value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private String payType;
    @ApiModelProperty(position = 31, value = "下单用户ID")
    private Integer userId;
    @ApiModelProperty(position = 81, value = "收货人")
    private String addresseeName;
    @ApiModelProperty(position = 101, value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(position = 102, value = "经销商姓名")
    private String distributorName;
    @ApiModelProperty(position = 28, value = "取消订单时间")
    private String cancelTime;
    @ApiModelProperty(position = 29, value = "取消订单原因")
    private String cancelReason;
    @ApiModelProperty(position = 17, value = "产品数量")
    private Integer count;
    @ApiModelProperty(position = 11, value = "订单金额")
    private BigDecimal fee;
    @ApiModelProperty(value = "手续费")
    private BigDecimal formalitiesFee;
    @ApiModelProperty(value = "手续费")
    private BigDecimal shouldFee;
    @ApiModelProperty(position = 4, value = "子状态(售后中状态):0-待审核(经销商)；1-待审核(总部)；2-待退货入库；3-待退款（财务）；4-售后失败；5-售后成功；")
    private Integer status;
    @ApiModelProperty(position = 74, value = "产品公司名称")
    private String productCompanyName;
    @ApiModelProperty(position = 74, value = "产品公司名称")
    private String subStatus;

    @ApiModelProperty(value = "退款流水号")
    private String refundTradeNo;
    @ApiModelProperty(value = "审核时间")
    private String reviewTime;
    @ApiModelProperty(value = "财务审核人")
    private String financer;
    @ApiModelProperty(value = "财务审核时间")
    private String financeTime;
    @ApiModelProperty(value = "应退金额")
    private BigDecimal shouldReturn;






//    @ApiModelProperty(position = 3, value = "状态：0-待付款；1-待审核；2-待发货；3-待出库；4-待收货；5-交易成功；6-售后中；7-交易关闭；8-已取消；")
//    private Integer status;
//
//    @ApiModelProperty(position = 5, value = "是否支付")
//    private Boolean pay;
//    @ApiModelProperty(position = 6, value = "支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败")
//    private Integer payStatus;
//
//    @ApiModelProperty(position = 8, value = "支付端：1-立即支付；2-货到付款；")
//    private Integer payTerminal;
//    @ApiModelProperty(position = 8, value = "线下支付凭证")
//    private String payCredential;
//    @ApiModelProperty(position = 8, value = "支付凭证提交时间")
//    private Date payCredentialSubmitTime;
//    @ApiModelProperty(position = 9, value = "是否他人代付：0、否；1、是")
//    private Boolean isReplacePay;
//    @ApiModelProperty(position = 9, value = "支付时间", example = "2018-12-28 11:00:00")
//    private Date payTime;
//    @ApiModelProperty(position = 10, value = "订单支付单号，第三方支付流水号")
//    private String tradeNo;
//
//    @ApiModelProperty(position = 12, value = "订单运费金额")
//    private BigDecimal logisticsFee;
//    @ApiModelProperty(position = 13, value = "产品ID")
//    private Integer productId;
//    @ApiModelProperty(position = 14, value = "产品价格", required = true)
//    private BigDecimal productPrice;
//    @ApiModelProperty(position = 15, required = true, value = "商品类型（大类）:1-实物商品；2-电子卡券；3-租赁商品；对应product表的type字段")
//    private Integer productType;
//    @ApiModelProperty(position = 16, value = "产品型号")
//    private String productModel;
//
//
//    @ApiModelProperty(position = 19, value = "订单总价")
//    private BigDecimal orderAmountFee;
//    @ApiModelProperty(position = 25, value = "商品总价")
//    private BigDecimal productAmountFee;
//
//    @ApiModelProperty(position = 20, value = "产品活动类型：1 普通产品， 2 折机商品，3-180产品  type 是租赁商品是有值；")
//    private Integer activityType;
//    @ApiModelProperty(position = 21, value = "订单审核时间", example = "2018-12-28 11:00:00")
//    private Date auditTime;
//    @ApiModelProperty(position = 22, value = "退款时间", example = "2018-12-28 11:00:00")
//    private Date refundTime;
//    @ApiModelProperty(position = 23, value = "分销商是否享受收益")
//    private Integer userSaleFlag;
//    @ApiModelProperty(position = 24, value = "订单是否已查看")
//    private String isLook;
//
//    @ApiModelProperty(position = 28, value = "取消订单时间")
//    private Date cancelTime;
//
//
//    @ApiModelProperty(position = 30, value = "收货地址ID")
//    private Integer addressId;
//
//    @ApiModelProperty(position = 31, value = "会员用户ID")
//    private Integer vipUserId;
//    @ApiModelProperty(position = 32, value = "经销商ID")
//    private Integer distributorId;
//    @ApiModelProperty(position = 33, value = "安装工ID")
//    private Integer engineerId;
//    @ApiModelProperty(position = 34, value = "服务站ID")
//    private Integer stationId;
//    @ApiModelProperty(position = 35, value = "服务站区县公司ID")
//    private Integer stationCompanyId;
//
//    @ApiModelProperty(position = 38, value = "是否删除")
//    private Boolean deleted;
//    @ApiModelProperty(position = 39, value = "备注")
//    private String remark;
//
//    @ApiModelProperty(position = 40, value = "发货时间", example = "2018-12-28 11:00:00")
//    private Date deliveryTime;
//    @ApiModelProperty(position = 41, value = "收货时间", example = "2018-12-28 11:00:00")
//    private Date receiveTime;
//    @ApiModelProperty(position = 42, value = "订单完成时间", example = "2018-12-28 11:00:00")
//    private Date completeTime;
//    @ApiModelProperty(position = 43, value = "订单创建时间", example = "2018-12-28 11:00:00")
//    private Date createTime;
//    @ApiModelProperty(position = 44, value = "订单更新时间", example = "2018-12-28 11:00:00")
//    private Date updateTime;
//
//    //水机详细信息
//    @ApiModelProperty(position = 60, value = "计费方式ID")
//    private Integer costId;
//    @ApiModelProperty(position = 61, value = "计费方式名称")
//    private String costName;
//    @ApiModelProperty(position = 62, value = "派单方式：1-手动派单；2-自动派单；")
//    private Integer dispatchType;
//    @ApiModelProperty(position = 63, value = "开户费，默认0")
//    private BigDecimal openAccountFee;
//    @ApiModelProperty(position = 64, value = "水机预约安装服务时间", example = "2018-12-28 11:00:00")
//    private Date serviceTime;
//
//    //产品详细信息
//    @ApiModelProperty(position = 71, value = "产品名称")
//    private String productName;
//    @ApiModelProperty(position = 72, value = "产品封面图片")
//    private String productImg;
//    @ApiModelProperty(position = 73, value = "产品公司ID")
//    private Integer productCompanyId;
//    @ApiModelProperty(position = 74, value = "产品公司名称")
//    private String productCompanyName;
//    @ApiModelProperty(position = 75, value = "产品类目ID（三级类目）")
//    private Integer productCategoryId;
//
//    @ApiModelProperty(position = 77, value = "产品类目ID（一级类目）")
//    private Integer productOneCategoryId;
//
//
//    //收货人详细信息
//
//    @ApiModelProperty(position = 82, value = "收货人手机号")
//    private String addresseePhone;
//
//    //下单用户详细信息
//    @ApiModelProperty(position = 91, value = "下单用户等级：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商")
//    private Integer userType;
//    @ApiModelProperty(position = 92, value = "下单用户等级名称")
//    private String userTypeName;
//    @ApiModelProperty(position = 93, value = "下单用户姓名")
//    private String userName;
//    @ApiModelProperty(position = 94, value = "下单用户手机号")
//    private String userPhone;
//
//    //下单用户详细信息
//    @ApiModelProperty(position = 95, value = "下单用户等级：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商")
//    private Integer vipUserType;
//    @ApiModelProperty(position = 96, value = "下单用户等级名称")
//    private String vipUserTypeName;
//    @ApiModelProperty(position = 97, value = "下单用户姓名")
//    private String vipUserName;
//    @ApiModelProperty(position = 98, value = "下单用户手机号")
//    private String vipUserPhone;
//
//    //经销商详细信息
//    @ApiModelProperty(position = 101, value = "经销商e家号")
//    private Integer distributorNumber;
//
//    @ApiModelProperty(position = 102, value = "经销商姓名")
//    private String distributorName;
//    @ApiModelProperty(position = 103, value = "经销商身份：1-代理商,2-经销商, 3-经销商+代理商")
//    private Integer distributorType;
//    @ApiModelProperty(position = 104, value = "经销商身份名称")
//    private String distributorTypeName;
//    @ApiModelProperty(position = 105, value = "经销商电话")
//    private String distributorPhone;
//
//
//    @ApiModelProperty(position = 108, value = "经销商子账号ID")
//    private Integer subDistributorId;
//    @ApiModelProperty(position = 108, value = "经销商子账号姓名")
//    private String subDistributorName;
//
//
//    @ApiModelProperty(position = 135, value = "订单详情状态:1、待付款，2、代付中，3、支付审核失败，4、支付审核中，5、待接单，6、待发货，7、订单取消中，8、出库中，9、已接单，10、待收货，11、已完成，12、待总部审核，13、待经销商审核，14、待退货，15、待退款，16、已退款，17、已取消，18、取消失败")
//    private Integer detailStatus;
//
//
//
//
//    @ApiModelProperty(value = "支付类型 1-线上，2-线下")
//    private Integer typePay;
//
//    @ApiModelProperty(value = "审核时间")
//    private Integer reviewTime;

}
