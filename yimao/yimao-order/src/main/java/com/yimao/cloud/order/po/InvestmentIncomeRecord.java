package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.InvestmentIncomeRecordDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 招商收益记录
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@Data
@Table(name = "investment_income_record")
public class InvestmentIncomeRecord{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long orderId;//订单id
    private Integer userId;//用户id
    private String userName;//用户名称
    private String userPhone;//用户手机号
    private Integer distributorId;//经销商id
    private Integer distributorLevel;//经销商级别
    private Integer distributorOrderType;//经销商订单类型：0-注册、1-升级、2-续费
    private Integer incomeRuleId;//收益规则id
    private Integer allotType;//分配规则：1-按比例分配 2-按金额分配
    private BigDecimal realPayment;//实付款
    private BigDecimal receivableMoney;//应收款
    private BigDecimal moreMoney;//多付款
    private Integer destDistributorLevel; //升级经销商的等级类型
    private Integer orderCompleteStatus;//订单完成状态：0-未完成 1-(已完成)可结算 2-已结算 3-已失效
    private Date orderCompleteTime;//订单完成时间
    private Date settlementTime;//结算时间
    private Date createTime;//创建时间
    private String creator;//创建人
    private Date updateTime;//更新时间
    private String updater;//更新人

    private String distributorName;//经销商姓名
    private String distributorAccount;//经销商账户
    private String province;//经销商省
    private String city;//经销商市
    private String region;//经销商区
    private String refereeName;//推荐人姓名
    private String refereeAccount;//推荐人账号
    private String paySubject;//付款主体(推荐人所在的区县级公司)
    private Integer payType;//支付方式：1-微信；2-支付宝；3-POS机；4-转账；
    private Date payTime;//支付时间
    private String tradeNo;//支付流水号/线上付款的流水号


    public InvestmentIncomeRecord() {
    }

    /**
     * 用业务对象InvestmentIncomeRecordDTO初始化数据库对象InvestmentIncomeRecord。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public InvestmentIncomeRecord(InvestmentIncomeRecordDTO dto) {
        this.id = dto.getId();
        this.orderId = dto.getOrderId();
        this.userId = dto.getUserId();
        this.userName = dto.getUserName();
        this.userPhone = dto.getUserPhone();
        this.distributorId = dto.getDistributorId();
        this.distributorLevel = dto.getDistributorLevel();
        this.distributorOrderType = dto.getDistributorOrderType();
        this.incomeRuleId = dto.getIncomeRuleId();
        this.allotType = dto.getAllotType();
        this.realPayment = dto.getRealPayment();
        this.receivableMoney = dto.getReceivableMoney();
        this.moreMoney = dto.getMoreMoney();
        this.destDistributorLevel = dto.getDestDistributorLevel();
        this.orderCompleteStatus = dto.getOrderCompleteStatus();
        this.orderCompleteTime = dto.getOrderCompleteTime();
        this.settlementTime = dto.getSettlementTime();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
        this.distributorName = dto.getDistributorName();
        this.distributorAccount = dto.getDistributorAccount();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.refereeName = dto.getRefereeName();
        this.refereeAccount = dto.getRefereeAccount();
        this.paySubject = dto.getPaySubject();
        this.payType = dto.getPayType();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象InvestmentIncomeRecordDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(InvestmentIncomeRecordDTO dto) {
        dto.setId(this.id);
        dto.setOrderId(this.orderId);
        dto.setUserId(this.userId);
        dto.setUserName(this.userName);
        dto.setUserPhone(this.userPhone);
        dto.setDistributorId(this.distributorId);
        dto.setDistributorLevel(this.distributorLevel);
        dto.setDistributorOrderType(this.distributorOrderType);
        dto.setIncomeRuleId(this.incomeRuleId);
        dto.setAllotType(this.allotType);
        dto.setRealPayment(this.realPayment);
        dto.setReceivableMoney(this.receivableMoney);
        dto.setMoreMoney(this.moreMoney);
        dto.setDestDistributorLevel(this.destDistributorLevel);
        dto.setOrderCompleteStatus(this.orderCompleteStatus);
        dto.setOrderCompleteTime(this.orderCompleteTime);
        dto.setSettlementTime(this.settlementTime);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
        dto.setDistributorName(this.distributorName);
        dto.setDistributorAccount(this.distributorAccount);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setRefereeName(this.refereeName);
        dto.setRefereeAccount(this.refereeAccount);
        dto.setPaySubject(this.paySubject);
        dto.setPayType(this.payType);
    }
}