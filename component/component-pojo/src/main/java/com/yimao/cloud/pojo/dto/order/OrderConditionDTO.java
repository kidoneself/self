package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author zhilin.he
 * @description 订单查询条件DTO
 * @date 2019/1/10 17:07
 **/
@Getter
@Setter
@ApiModel(description = "订单查询条件")
public class OrderConditionDTO implements Serializable {
    private static final long serialVersionUID = -1887571142666581456L;

    @ApiModelProperty(value = "订单号")
    private Long id;
    @ApiModelProperty(value = "主订单号")
    private Long mainOrderId;
    @ApiModelProperty(value = "查询订单号键（主订单号或子订单号）")
    private Long idKey;
    @ApiModelProperty(value = "云平台工单号或者HRA体检号")
    private String refer;
    @ApiModelProperty(value = "0-待付款 1-待审核 2-待发货 3-待出库  4-待收货 5-交易成功 6-售后中 7-交易关闭 8-已取消")
    private Integer status;
    @ApiModelProperty(value = "子状态(售后中状态):0-待审核(经销商)；1-待审核(总部),2-待退货入库,3-待退款(财务),4-售后失败,5-售后成功")
    private Integer subStatus;
    @ApiModelProperty(value = "下单用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户的Mid")
    private Integer mid;
    @ApiModelProperty(value = "用户身份 1-经销商身份 2-其他身份")
    private Integer userIdentity;
    @ApiModelProperty(value = "用户等级：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商")
    private Integer userType;
    @ApiModelProperty(value = "产品类目id键")
    private Integer productCategoryIdKey;
    @ApiModelProperty(value = "产品活动类型：1 普通产品， 2 折机商品，3-180产品  type 是租赁商品是有值")
    private Integer activityType;
    @ApiModelProperty(value = "订单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；")
    private Integer terminal;
    @ApiModelProperty(value = "支付状态:1、未支付；2、待审核；3、支付成功；4、支付失败")
    private Integer payStatus;
    @ApiModelProperty(value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;
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
    @ApiModelProperty(value = "经销商id")
    private Integer distributorId;
    @ApiModelProperty(value = "健康大使id(分销商id)")
    private Integer ambassadorId;
    @ApiModelProperty(value = "最小订单金额")
    private BigDecimal minAmountFee;
    @ApiModelProperty(value = "最大订单金额")
    private BigDecimal maxAmountFee;
    @ApiModelProperty(value = "订单开始时间", example = "2018-12-28 11:00:00")
    private Date beginTime;
    @ApiModelProperty(value = "订单结束时间", example = "2018-12-28 11:00:00")
    private Date endTime;
    @ApiModelProperty(value = "订单完成开始时间", example = "2018-12-28 11:00:00")
    private Date beginCompleteTime;
    @ApiModelProperty(value = "订单完成结束时间", example = "2018-12-28 11:00:00")
    private Date endCompleteTime;
    @ApiModelProperty(value = "发货开始时间", example = "2018-12-28 11:00:00")
    private Date beginDeliveryTime;
    @ApiModelProperty(value = "发货结束时间", example = "2018-12-28 11:00:00")
    private Date endDeliveryTime;
    /*@ApiModelProperty(value = "发货时间")
    private Date deliveryTime;*/
    @ApiModelProperty(value = "支付开始时间", example = "2018-12-28 11:00:00")
    private Date beginPayTime;
    @ApiModelProperty(value = "支付结束时间", example = "2018-12-28 11:00:00")
    private Date endPayTime;
    @ApiModelProperty(value = "分销商是否享受收益")
    private Integer userSaleFlag;
    @ApiModelProperty(value = "服务站省")
    private String stationProvince;
    @ApiModelProperty(value = "服务站市")
    private String stationCity;
    @ApiModelProperty(value = "服务站区")
    private String stationRegion;
    @ApiModelProperty(value = "子订单id集合")
    List<Integer> idList;
    @ApiModelProperty(value = "服务站id集合")
    List<Integer> stationIdList;

    @ApiModelProperty(value = "对账产品公司id的Key")
    private Integer productCompanyIdKey;
    @ApiModelProperty(value = "产品公司id")
    private Integer productCompanyId;
    @ApiModelProperty(value = "订单支付单号，第三方支付流水号")
    private String tradeNo;
    @ApiModelProperty(value = "子订单类型：1-为自己下单；2-为客户下单")
    private Integer subType;
    @ApiModelProperty(value = "续费单号")
    private String renewId;
    @ApiModelProperty(value = "设备SN码")
    private String snCode;

    //售后条件
    @ApiModelProperty(value = "售后单号")
    private Long orderSalesId;
    @ApiModelProperty(value = "售后申请开始时间", example = "2018-12-28 11:00:00")
    private Date beginApplyTime;
    @ApiModelProperty(value = "售后申请结束时间", example = "2018-12-28 11:00:00")
    private Date endApplyTime;
    @ApiModelProperty(value = "售后状态：1-待审核(物资) ， 2-待退货入库 ，3-待退款(财务) ， 4-售后失败， 5-售后成功")
    private Integer salesStatus;
    @ApiModelProperty(value = "业务部门审核状态：1-审核通过；2-审核不通过")
    private Integer businessAuditStatus;
    @ApiModelProperty(value = "物质确认审核状态：1-审核通过；2-审核不通过（1-收货确认；2-未确认）")
    private Integer buyAuditStatus;
    @ApiModelProperty(value = "财务复核状态：1-审核通过；2-审核不通过")
    private Integer financeAuditStatus;
    @ApiModelProperty(value = "400客服审核状态或提交物流：1-审核通过；2-审核不通过（1-已提交物流，2-未提交）")
    private Integer customerServiceAuditStatus;
    @ApiModelProperty(value = "售后申请端：1-终端app；2-微信公众号；3-经销商APP；4-小程序；5-翼猫业务系统；")
    private Integer salesTerminal;
    @ApiModelProperty(value = "售后类型：1.取消订单退款（未收货），2.申请退货退款（已收货）")
    private Integer salesType;
    @ApiModelProperty(value = "商品类型（大类）:1实物商品，2电子卡券，3租赁商品 对应产品:product: type")
    private Integer productType;

    //审核类型
    @ApiModelProperty(value = "审核类型  1.取消订单退款（未收货），2.申请退货退款（已收货），3：提现")
    private Integer auditType;
    @ApiModelProperty(value = "子审核类型  1-业务部门审核，2-400客服审核（租赁商品），3-400客服提交物流，4-物资审核")
    private Integer auditSubType;

    @ApiModelProperty(value = "0-待付款 2-待发货 4-待收货 5-已完成")
    private Integer frontStatus;

    @ApiModelProperty(value = "用户id集合")
    private List<Integer> ids;

    @ApiModelProperty(value = "会员用户ID（检索条件）")
    private Integer vipUserId;

    @ApiModelProperty(value = "经销商id集合")
    private List<Integer> distributorIds;

    @ApiModelProperty(value = "操作类型: -1-全部 0-待付款 1-待发货 2-待收货 3-已完成 4-退款/退货/待审核 5-待退货 6-待退款 7-取消记录")
    private Integer operationType;

    @ApiModelProperty(value = "下单时间类型：1、昨日；2、上周；3、30天内；4、3个月内；5、今年；6、上一年")
    private Integer timeType;

    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "产品一级类目id")
    private Integer productFirstCategoryId;
    @ApiModelProperty(value = "支付端：1-立即支付；2-货到付款；")
    private Integer payTerminal;
    @ApiModelProperty(value = "服务站查询区域id集合")
    private Set<Integer> areas;

    // ---------- 站务系统概况跳转到相关列表需传参数 ---------------
    @ApiModelProperty(value = "是否是有效产品销售订单")
    private Boolean isValidOrder;
    @Transient
    private Integer pageSize;
    @Transient
    private Long queryId;
    @Transient
    private String createTime;

    @Transient
    private Integer source; //1-公众号 2-经销商APP
    @Transient
    private Integer cstatus; //作为订单列表ResultMap的参数传递
    @Transient
    private Integer csubStatus; //作为订单列表ResultMap的参数传递

    @ApiModelProperty(value = "查询类型 0-全部（默认） 1-主账户 2-子账户")
    private Integer queryType;
    @ApiModelProperty(value = "经销商子账号ID，当queryType=2时，此为必传")
    private Integer subDistributorId;


    @Override
    public String toString() {
        return "OrderConditionDTO{" +
                "id=" + id +
                ", mainOrderId=" + mainOrderId +
                ", idKey=" + idKey +
                ", refer='" + refer + '\'' +
                ", status=" + status +
                ", subStatus=" + subStatus +
                ", userId=" + userId +
                ", userType=" + userType +
                ", productCategoryIdKey=" + productCategoryIdKey +
                ", activityType=" + activityType +
                ", terminal=" + terminal +
                ", payStatus=" + payStatus +
                ", payType=" + payType +
                ", addressId=" + addressId +
                ", receiver='" + receiver + '\'' +
                ", receiveMobile='" + receiveMobile + '\'' +
                ", receiveProvince='" + receiveProvince + '\'' +
                ", receiveCity='" + receiveCity + '\'' +
                ", receiveRegion='" + receiveRegion + '\'' +
                ", distributorId=" + distributorId +
                ", ambassadorId=" + ambassadorId +
                ", minAmountFee=" + minAmountFee +
                ", maxAmountFee=" + maxAmountFee +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", beginCompleteTime=" + beginCompleteTime +
                ", endCompleteTime=" + endCompleteTime +
                ", beginDeliveryTime=" + beginDeliveryTime +
                ", endDeliveryTime=" + endDeliveryTime +
                ", beginPayTime=" + beginPayTime +
                ", endPayTime=" + endPayTime +
                ", userSaleFlag=" + userSaleFlag +
                ", stationProvince='" + stationProvince + '\'' +
                ", stationCity='" + stationCity + '\'' +
                ", stationRegion='" + stationRegion + '\'' +
                ", idList=" + idList +
                ", stationIdList=" + stationIdList +
                ", productCompanyIdKey=" + productCompanyIdKey +
                ", productCompanyId=" + productCompanyId +
                ", tradeNo='" + tradeNo + '\'' +
                ", subType=" + subType +
                ", renewId=" + renewId +
                ", snCode='" + snCode + '\'' +
                ", orderSalesId=" + orderSalesId +
                ", beginApplyTime=" + beginApplyTime +
                ", endApplyTime=" + endApplyTime +
                ", salesStatus=" + salesStatus +
                ", businessAuditStatus=" + businessAuditStatus +
                ", buyAuditStatus=" + buyAuditStatus +
                ", financeAuditStatus=" + financeAuditStatus +
                ", customerServiceAuditStatus=" + customerServiceAuditStatus +
                ", salesTerminal=" + salesTerminal +
                ", salesType=" + salesType +
                ", productType=" + productType +
                ", auditType=" + auditType +
                ", auditSubType=" + auditSubType +
                ", frontStatus=" + frontStatus +
                ", ids=" + ids +
                ", vipUserId=" + vipUserId +
                ", distributorIds=" + distributorIds +
                ", operationType=" + operationType +
                ", timeType=" + timeType +
                ", productName='" + productName + '\'' +
                '}';
    }
}
