package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 子订单
 *
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@Table(name = "order_sub")
@Getter
@Setter
public class OrderSub {

    @Id
    private Long id;                   //订单号
    private Long mainOrderId;          //主订单号

    private Integer status;            //状态：0-待付款；1-待审核；2-待发货；3-待出库；4-待收货；5-交易成功；6-售后中；7-交易关闭；8-已取消；
    private Integer subStatus;         //子状态(售后中状态):0-待审核(经销商)；1-待审核(总部)；2-待退货入库；3-待退款（财务）；4-售后失败；5-售后成功；
    private Boolean pay;               // 是否支付：0、未支付；1、已支付
    private Integer payType;           //支付方式：1-微信；2-支付宝；3-POS机；4-转账；
    private Integer payTerminal;       //支付类型：1-立即支付；2-货到付款；
    private String payCredential;          //线下支付凭证
    private Date payCredentialSubmitTime;  //支付凭证提交时间
    private Boolean isReplacePay;      // 是否他人代付：0、否；1、是
    private Integer payStatus;         //支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败
    private Date payTime;              //支付时间
    private String tradeNo;            //订单支付单号，第三方支付流水号
    private BigDecimal fee;            //订单金额
    private BigDecimal logisticsFee;   //订单运费金额
    private Integer productId;         //产品ID
    private BigDecimal productPrice;   //单个商品价格
    private Integer productType;       //商品类型（大类）:1-实物商品；2-电子卡券；3-租赁商品；对应product表的type字段
    private String productModel;       //产品型号
    private Integer count;             //所购买的商品数量

    private String refer;               //云平台工单号

    private Integer terminal;          //订单来源：1-终端app；2-微信公众号；3-翼猫APP；4-小程序；

    private Integer activityType;      //产品活动类型：1 普通产品， 2 折机商品，3-180产品  5-限时抢购 type 是租赁商品是有值；
    private Integer activityId;        //产品活动ID，对应productActivity表主键
    private Date auditTime;            //订单审核时间
    private Date refundTime;           //退款时间
    private Integer userSaleFlag;      //分销商是否享受收益
    private String isLook;             //订单是否已查看

    private Date cancelTime;           //取消订单时间
    private String cancelReason;       //取消订单原因

    private Integer addressId;         //收货地址ID
    private Integer userId;            //下单用户ID
    private Integer vipUserId;         //会员用户ID
    private Integer distributorId;     //经销商ID
    private Integer engineerId;        //安装工ID
    private Integer stationId;         //服务站ID
    private Integer stationCompanyId;  //服务站区县公司ID

    private Boolean deleted;           //是否删除

    private String remark;             //备注

    private Date deliveryTime;         //发货时间
    private Date receiveTime;          //收货时间
    private Date completeTime;         //订单完成时间
    private Date createTime;           //订单创建时间
    private Date updateTime;           //订单更新时间

    public OrderSub() {
    }

    /**
     * 用业务对象OrderSubDTO初始化数据库对象OrderSub。
     *
     * @param dto 业务对象
     */
    public OrderSub(OrderSubDTO dto) {
        this.id = dto.getId();
        this.mainOrderId = dto.getMainOrderId();
        this.status = dto.getStatus();
        this.subStatus = dto.getSubStatus();
        this.pay = dto.getPay();
        this.payStatus=dto.getPayStatus();
        this.payType = dto.getPayType();
        this.payTerminal = dto.getPayTerminal();
        this.payCredential = dto.getPayCredential();
        this.payCredentialSubmitTime = dto.getPayCredentialSubmitTime();
        this.isReplacePay = dto.getIsReplacePay();
        this.payTime = dto.getPayTime();
        this.tradeNo = dto.getTradeNo();
        this.fee = dto.getFee();
        this.logisticsFee = dto.getLogisticsFee();
        this.productId = dto.getProductId();
        this.productPrice= dto.getProductPrice();
        this.productType = dto.getProductType();
        this.productModel = dto.getProductModel();
        this.count = dto.getCount();
        this.terminal = dto.getTerminal();
        this.activityType = dto.getActivityType();
        this.auditTime = dto.getAuditTime();
        this.refundTime = dto.getRefundTime();
        this.userSaleFlag = dto.getUserSaleFlag();
        this.isLook = dto.getIsLook();
        this.cancelTime = dto.getCancelTime();
        this.cancelReason = dto.getCancelReason();
        this.addressId = dto.getAddressId();
        this.userId = dto.getUserId();
        this.distributorId = dto.getDistributorId();
        this.engineerId = dto.getEngineerId();
        this.stationId = dto.getStationId();
        this.stationCompanyId = dto.getStationCompanyId();
        this.deleted = dto.getDeleted();
        this.remark = dto.getRemark();
        this.deliveryTime = dto.getDeliveryTime();
        this.receiveTime = dto.getReceiveTime();
        this.completeTime = dto.getCompleteTime();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.refer = dto.getRefer();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象OrderSubDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(OrderSubDTO dto) {
        dto.setId(this.id);
        dto.setMainOrderId(this.mainOrderId);
        dto.setStatus(this.status);
        dto.setSubStatus(this.subStatus);
        dto.setPay(this.pay);
        dto.setPayStatus(this.payStatus);
        dto.setPayType(this.payType);
        dto.setPayTerminal(this.payTerminal);
        dto.setPayCredential(this.payCredential);
        dto.setPayCredentialSubmitTime(this.payCredentialSubmitTime);
        dto.setIsReplacePay(this.isReplacePay);
        dto.setPayTime(this.payTime);
        dto.setTradeNo(this.tradeNo);
        dto.setFee(this.fee);
        dto.setLogisticsFee(this.logisticsFee);
        dto.setProductId(this.productId);
        dto.setProductType(this.productType);
        dto.setProductPrice(this.productPrice);
        dto.setProductModel(this.productModel);
        dto.setCount(this.count);
        dto.setTerminal(this.terminal);
        dto.setActivityType(this.activityType);
        dto.setAuditTime(this.auditTime);
        dto.setRefundTime(this.refundTime);
        dto.setUserSaleFlag(this.userSaleFlag);
        dto.setIsLook(this.isLook);
        dto.setCancelTime(this.cancelTime);
        dto.setCancelReason(this.cancelReason);
        dto.setAddressId(this.addressId);
        dto.setUserId(this.userId);
        dto.setDistributorId(this.distributorId);
        dto.setEngineerId(this.engineerId);
        dto.setStationId(this.stationId);
        dto.setStationCompanyId(this.stationCompanyId);
        dto.setDeleted(this.deleted);
        dto.setRemark(this.remark);
        dto.setDeliveryTime(this.deliveryTime);
        dto.setReceiveTime(this.receiveTime);
        dto.setCompleteTime(this.completeTime);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setRefer(this.refer);
    }
}