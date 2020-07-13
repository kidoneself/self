package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 经销商订单
 *
 * @author Lizhqiang
 * @date 2018/12/17
 */
@Table(name = "distributor_order")
@Getter
@Setter
public class DistributorOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//订单号
    private Integer orderSouce; //订单来源:0-H5页面、1-经销商app、2-翼猫业务系统
    private Integer orderType; //订单类型:0-注册、1-升级、2-续费
    private String distributorAccount; //经销商账号
    private String mobile; //手机号
    private Integer distributorId;
    private BigDecimal price; //价格
    private Integer roleId;
    private Integer destRoleId;
    private Integer roleLevel; //经销商类型等级 50体验版、350微创版、650个人版、950企业版
    private Integer destRoleLevel; //升级经销商类型等级 50体验版、350微创版、650个人版、950企业版
    private Integer payState; //支付状态 1-未支付、2-已支付、3-支付失败 4-待审核  6-无需支付
    private Integer payType; //支付类型：1-微信；2-支付宝；3-POS机；4-转账；
    private Date payTime; //支付时间
    private Integer orderState; //订单状态 0-待审核、1-已完成、2-待付款 3-已关闭
    private Integer financialState; //财务审核状态:0-未审核、1-审核通过、2-审核不通过、3-无需审核
    private Integer enterpriseState; //企业审核状态:0-未审核、1-审核通过、2-审核不通过、3-无需审核
    private Date completionTime; //完成时间
    private Integer periodValidity; //升级剩余有效期
    private String payRecord; //支付凭证/pos机付款的凭证
    private String tradeNo; //支付流水号/线上付款的流水号
    private Date updateTime;
    private Date createTime;
    private Integer quotaNumber; //升级应给配额
    private Integer creator;  //创建人id（app创建新账号人的e家号、管理后台创建经销商的管理员ID）
    private Integer stationCompanyId; //服务站公司id
    private String stationCompanyName; //服务站公司名称
    private String financialAuditor; //财务审核人
    private Date financialAuditTime; //财务审核时间
    private String enterpriseAuditor; //企业审核人
    private Date enterpriseAuditTime; //企业审核时间
    private Integer recommendId;
    private String recommendName;
    private String distributorIdCard;
    private String province;
    private String city;
    private String region;
    private String address;
    private String realName;
    private String oldOrderId;//老系统orderId
    private Integer areaId; //地区id

    public DistributorOrder() {
    }

    /**
     * 用业务对象DistributorOrderDTO初始化数据库对象DistributorOrder。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public DistributorOrder(DistributorOrderDTO dto) {
        this.id = dto.getId();
        this.orderSouce = dto.getOrderSouce();
        this.orderType = dto.getOrderType();
        this.distributorAccount = dto.getDistributorAccount();
        this.mobile = dto.getMobile();
        this.distributorId = dto.getDistributorId();
        this.price = dto.getPrice();
        this.roleId = dto.getRoleId();
        this.destRoleId = dto.getDestRoleId();
        this.roleLevel = dto.getRoleLevel();
        this.destRoleLevel = dto.getDestRoleLevel();
        this.payState = dto.getPayState();
        this.payType = dto.getPayType();
        this.payTime = dto.getPayTime();
        this.orderState = dto.getOrderState();
        this.financialState = dto.getFinancialState();
        this.enterpriseState = dto.getEnterpriseState();
        this.completionTime = dto.getCompletionTime();
        this.periodValidity = dto.getPeriodValidity();
        this.payRecord = dto.getPayRecord();
        this.tradeNo = dto.getTradeNo();
        this.updateTime = dto.getUpdateTime();
        this.createTime = dto.getCreateTime();
        this.quotaNumber = dto.getQuotaNumber();
        this.creator = dto.getCreator();
        this.stationCompanyId = dto.getStationCompanyId();
        this.stationCompanyName = dto.getStationCompanyName();
        this.financialAuditor = dto.getFinancialAuditor();
        this.financialAuditTime = dto.getFinancialAuditTime();
        this.enterpriseAuditor = dto.getEnterpriseAuditor();
        this.enterpriseAuditTime = dto.getEnterpriseAuditTime();
        this.recommendId = dto.getRecommendId();
        this.recommendName = dto.getRecommendName();
        this.distributorIdCard = dto.getDistributorIdCard();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.realName = dto.getRealName();
        this.oldOrderId = dto.getOldOrderId();
        this.areaId = dto.getAreaId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象DistributorOrderDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(DistributorOrderDTO dto) {
        dto.setId(this.id);
        dto.setOrderSouce(this.orderSouce);
        dto.setOrderType(this.orderType);
        dto.setDistributorAccount(this.distributorAccount);
        dto.setMobile(this.mobile);
        dto.setDistributorId(this.distributorId);
        dto.setPrice(this.price);
        dto.setRoleId(this.roleId);
        dto.setDestRoleId(this.destRoleId);
        dto.setRoleLevel(this.roleLevel);
        dto.setDestRoleLevel(this.destRoleLevel);
        dto.setPayState(this.payState);
        dto.setPayType(this.payType);
        dto.setPayTime(this.payTime);
        dto.setOrderState(this.orderState);
        dto.setFinancialState(this.financialState);
        dto.setEnterpriseState(this.enterpriseState);
        dto.setCompletionTime(this.completionTime);
        dto.setPeriodValidity(this.periodValidity);
        dto.setPayRecord(this.payRecord);
        dto.setTradeNo(this.tradeNo);
        dto.setUpdateTime(this.updateTime);
        dto.setCreateTime(this.createTime);
        dto.setQuotaNumber(this.quotaNumber);
        dto.setCreator(this.creator);
        dto.setStationCompanyId(this.stationCompanyId);
        dto.setStationCompanyName(this.stationCompanyName);
        dto.setFinancialAuditor(this.financialAuditor);
        dto.setFinancialAuditTime(this.financialAuditTime);
        dto.setEnterpriseAuditor(this.enterpriseAuditor);
        dto.setEnterpriseAuditTime(this.enterpriseAuditTime);
        dto.setRecommendId(this.recommendId);
        dto.setRecommendName(this.recommendName);
        dto.setDistributorIdCard(this.distributorIdCard);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setRealName(this.realName);
        dto.setOldOrderId(this.oldOrderId);
        dto.setAreaId(this.areaId);
    }
}
