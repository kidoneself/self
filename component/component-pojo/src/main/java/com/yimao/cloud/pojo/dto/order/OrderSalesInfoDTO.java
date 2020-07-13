package com.yimao.cloud.pojo.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhilin.he
 * @description 售后信息表
 * @date 2019/1/28 10:55
 **/
@Data
@ApiModel(description = "售后信息表")
public class OrderSalesInfoDTO {

    @ApiModelProperty(value = "售后单号")
    private Long id;
    @ApiModelProperty(value = "订单号")
    private Long mainOrderId;
    @ApiModelProperty(value = "子订单号")
    private Long orderId;
    @ApiModelProperty(value = "售后状态:0-待审核(经销商)  1-待审核(总部)  2-待退货入库  3-待退款(财务)  4-售后失败 5-售后成功；")
    private Integer status;
    @ApiModelProperty(value = "业务部门审核状态：1-审核通过；2-审核不通过")
    private Integer businessAuditStatus;
    @ApiModelProperty(value = "物质确认审核状态：1-审核通过；2-审核不通过（1-收货确认；2-未确认）")
    private Integer buyAuditStatus;
    @ApiModelProperty(value = "财务复核状态：1-审核通过；2-审核不通过")
    private Integer financeAuditStatus;
    @ApiModelProperty(value = "400客服审核状态或提交物流：1-审核通过；2-审核不通过（1-已提交物流，2-未提交）")
    private Integer customerServiceAuditStatus;
    @ApiModelProperty(value = "售后类型：1.取消订单退款（未收货），2.申请退货退款（已收货）")
    private Integer salesType;
    @ApiModelProperty(value = "商品类型（大类）:1实物商品，2电子卡券，3租赁商品 对应产品:product: type")
    private Integer productType;
    @ApiModelProperty(value = "产品id")
    private Integer productId;
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "产品类目名称")
    private String productCategoryName;
    @ApiModelProperty(value = "产品公司名称")
    private String productCompanyName;
    @ApiModelProperty(value = "售后发起端")
    private Integer terminal;
    @ApiModelProperty(value = "数量")
    private Integer num;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户身份")
    private Integer userType;
    @ApiModelProperty(value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;
    @ApiModelProperty(value = "支付金额")
    private BigDecimal payFee;
    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundFee;
    @ApiModelProperty(value = "实退金额")
    private BigDecimal realRefundFee;
    @ApiModelProperty(value = "手续费")
    private BigDecimal formalitiesFee;
    @ApiModelProperty(value = "退款原因")
    private String refundReason;
    @ApiModelProperty(value = "退款审核通过时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refundTime;
    @ApiModelProperty(value = "企业付款成功，返回的流水号")
    private String refundTradeNo;
    @ApiModelProperty(value = "退款到账时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date accountTime;
    @ApiModelProperty(value = "物流单号")
    private String logisticsNo;
    @ApiModelProperty(value = "物流公司编号")
    private String logisticsCompanyNo;
    @ApiModelProperty(value = "物流公司名称")
    private String logisticsCompanyName;
    @ApiModelProperty(value = "申请退单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @ApiModelProperty(value = "业务部门审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date businessAuditTime;
    @ApiModelProperty(value = "业务部门审核人")
    private String businessman;
    @ApiModelProperty(value = "业务部门审核原因")
    private String businessReason;
    @ApiModelProperty(value = "物资审核时间")
    private Date buyAuditTime;
    @ApiModelProperty(value = "物资审核人")
    private String buyer;
    @ApiModelProperty(value = "物资审核原因")
    private String buyAuditReason;
    @ApiModelProperty(value = "财务审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date financeTime;
    @ApiModelProperty(value = "财务审核人")
    private String financer;
    @ApiModelProperty(value = "财务审核原因")
    private String financeReason;
    @ApiModelProperty(value = "400客服审核或提交物流时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date customerServiceTime;
    @ApiModelProperty(value = "400客服审核人或提交物流人员")
    private String customerService;
    @ApiModelProperty(value = "400客服审核或提交物流原因")
    private String customerServiceReason;
    @ApiModelProperty(value = "是否关闭售后：0-未取消，1-取消")
    private Integer deleted;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "审核方:如：经销商、总部")
    private String auditee;

    //订单信息
    @ApiModelProperty(value = "0-待付款 1-待审核 2-待发货 3-待出库  4-待收货 5-交易成功 6-售后中 7-交易关闭 8-已取消")
    private Integer orderStatus;
    @ApiModelProperty(value = "子状态(售后中状态):0-待审核(经销商)，1-待审核(总部),2-待退货入库,3-待退款(财务),4-售后失败,5-售后成功")
    private Integer subStatus;
    @ApiModelProperty(value = "是否支付")
    private Boolean pay;
    @ApiModelProperty(value = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;
    @ApiModelProperty(value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer orderPayType;
    @ApiModelProperty(value = "订单支付单号，第三方支付流水号")
    private String tradeNo;
    @ApiModelProperty(value = "订单金额")
    private BigDecimal amountFee;
    @ApiModelProperty(value = "订单运费")
    private BigDecimal logisticsFee;
    @ApiModelProperty(value = "支付端 1:经销商支付  2:其他(他人代付)  3:用户支付")
    private Integer payTerminal;
    @ApiModelProperty(value = "订单创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderCreateTime;
    @ApiModelProperty(value = "云平台工单号或者HRA体检号")
    private String workOrder;
    //    @ApiModelProperty(value = "订单类型：0-其他；1-智能净水；2-健康食品；3-生物理疗；4-健康睡眠；5-健康体检；")
//    private Integer orderType;
    @ApiModelProperty(value = "产品公司id")
    private Integer productCompanyId;
    @ApiModelProperty(value = "产品封面图片")
    private String productImg;
    @ApiModelProperty(value = "产品型号")
    private String productModel;
    @ApiModelProperty(value = "产品类目id3级")
    private Integer productCategoryId;
    @ApiModelProperty(value = "产品类目id1级")
    private Integer productCategoryId1;
    @ApiModelProperty(value = "产品类目id2级")
    private Integer productCategoryId2;
    @ApiModelProperty(value = "计费方式id")
    private Integer costId;
    @ApiModelProperty(value = "计费方式类型")
    private Integer costCode;
    @ApiModelProperty(value = "计费方式名称")
    private String costName;
    @ApiModelProperty(value = "订单数量")
    private Integer orderNum;
    @ApiModelProperty(value = "水机预约安装服务时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date serviceTime;
    @ApiModelProperty(value = "订单完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date completeTime;
    @ApiModelProperty(value = "收货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receiveTime;
    @ApiModelProperty(value = "订单更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @ApiModelProperty(value = "订单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；")
    private Integer orderTerminal;
    @ApiModelProperty(value = "收货人地址id")
    private Integer addressId;
    @ApiModelProperty(value = "收货人")
    private String receiver;
    @ApiModelProperty(value = "收货人手机号")
    private String receiveMobile;
    @ApiModelProperty(value = "收货人省")
    private String receiveProvince;
    @ApiModelProperty(value = "收货人市")
    private String receiveCity;
    @ApiModelProperty(value = "收货人区")
    private String receiveRegion;
    @ApiModelProperty(value = "收货人街道")
    private String receiveStreet;
    @ApiModelProperty(value = "发货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;
    @ApiModelProperty(value = "是否删除")
    private Boolean orderDeleted;
    @ApiModelProperty(value = "开户费 默认0")
    private BigDecimal openAccountFee;
    @ApiModelProperty(value = "服务站id")
    private Integer stationId;
    @ApiModelProperty(value = "服务站名称")
    private String stationName;
    @ApiModelProperty(value = "安装工所属服务站公司名称(财务需要）")
    private String stationCompany;
    @ApiModelProperty(value = "服务站联系电话")
    private String stationPhone;
    @ApiModelProperty(value = "服务站所属省")
    private String stationProvince;
    @ApiModelProperty(value = "服务站所属市")
    private String stationCity;
    @ApiModelProperty(value = "服务站所属区")
    private String stationRegion;
    @ApiModelProperty(value = "派单类型：0.自动派单，1，手动派单")
    private Integer dispatchType;
    @ApiModelProperty(value = "安装工id")
    private String customerId;
    @ApiModelProperty(value = "安装工姓名")
    private String customerName;
    @ApiModelProperty(value = "安装工电话")
    private String customerMobile;
    @ApiModelProperty(value = "安装工接单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date customerTime;
    @ApiModelProperty(value = "接单状态")
    private Integer customerStatus;
    @ApiModelProperty(value = "产品活动类型：1 普通产品， 2 折机商品，3-180产品  type 是租赁商品是有值")
    private Integer activityType;
    @ApiModelProperty(value = "订单审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditTime;
    @ApiModelProperty(value = "经销商是否删除")
    private Boolean distributorDelete;
    @ApiModelProperty(value = "分销商是否享受收益")
    private Integer userSaleFlag;
    @ApiModelProperty(value = "备注")
    private String orderRemark;
    @ApiModelProperty(value = "订单是否已查看")
    private String isLook;
    @ApiModelProperty(value = "健康大使id")
    private Integer ambassadorId;
    @ApiModelProperty(value = "经销商ID")
    private Integer distributorId;
    @ApiModelProperty(value = "经销商身份：1-代理商,2-经销商, 3-经销商+代理商")
    private Integer distributorType;
    @ApiModelProperty(value = "取消订单原因")
    private String cancelledReason;
    @ApiModelProperty(value = "取消订单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancelledTime;
    @ApiModelProperty(value = "安装完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date customerCompleteTime;
    @ApiModelProperty(value = "下单用户姓名")
    private String realName;
    @ApiModelProperty(value = "子订单类型：1-为自己下单；2-为客户下单")
    private Integer subType;


    //审核信息
    @ApiModelProperty(value = "审核id")
    private Long auditId;
    @ApiModelProperty(value = "售后单号")
    private Long salesId;
    @ApiModelProperty(value = "审核类型  1.取消订单退款（未收货），2.申请退货退款（已收货），3：提现")
    private Integer auditType;
    @ApiModelProperty(value = "子审核类型  1-业务部门审核，2-400客服审核（租赁商品），3-400客服提交物流，4-物资审核")
    private Integer auditSubType;
    @ApiModelProperty(value = "操作状态名")
    private String operation;
    @ApiModelProperty(value = "菜单名")
    private String menuName;
    @ApiModelProperty(value = "操作IP")
    private String ip;
    @ApiModelProperty(value = "操作人")
    private Integer creator;
    @ApiModelProperty(value = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditCreateTime;
    @ApiModelProperty(value = "审核不通过原因")
    private String auditReason;
    @ApiModelProperty(value = "详情描述")
    private String detailReason;


}
