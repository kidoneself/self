package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.OrderRenewDTO;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 续费单
 *
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@Table(name = "order_renew")
@Data
public class OrderRenew {

    @Id
    private String id;//续费单号
    private String workOrderId;//工单号
    private Long mainOrderId;//原始主订单号
    private Long orderId;//原始订单号
    private String tradeNo;//订单支付单号，第三方支付流水号
    private BigDecimal amountFee;//费用金额
    private String province;           //省
    private String city;               //市
    private String region;             //区县
    private Integer times;//第几次续费
    private Integer productFirstCategoryId;//产品一级类目id
    private String productFirstCategoryName;//产品一级类目名称
    private Integer productSecondCategoryId;//产品二级类目id
    private String productSecondCategoryName;//产品二级类目名称
    private Integer productCategoryId;//产品三级类目id
    private String productCategoryName;//产品三级类目名称
    private Integer productCompanyId;//产品公司id
    private String productCompanyName;//产品公司名称
    private Integer costId;//计费模板id
    private Integer costType;//计费方式：1-流量计费；2-包年计费；
    private String costTypeName;//计费方式：1-流量计费；2-包年计费；
    private String costName;//计费模板名称
    private Integer lastCostId;//上一次计费模板id
    private Integer lastCostType;//上一次计费方式：1-按流量计费；2-按时间计费；
    private String lastCostTypeName;//上一次计费方式：1-按流量计费；2-按时间计费；
    private String lastCostName;//上一次计费模板名称
    private Integer distributorId;//经销商ID
    private Integer distributorType;//经销商类型
    private String distributorTypeName;//经销商类型名称
    private String distributorAccount;//经销商账号
    private String distributorIdCard;//经销商身份证号码
    private String distributorName;//经销商名称
    private String distributorPhone;//经销商名称
    private String distributorProvince;//经销商所在省
    private String distributorCity;//经销商所在市
    private String distributorRegion;//经销商所在区
    private String distributorStationName;//经销商服务站名称
    private String distributorRecommendName;
    private String distributorRecommendProvince;
    private String distributorRecommendCity;
    private String distributorRecommendRegion;
    private String distributorRecommendStationName;//经销商推荐人服务站名称
    private Integer engineerId;//安装工程师ID
    private String engineerName;//安装工姓名
    private String engineerPhone;//安装工手机号
    private String engineerStationName;//安装工服务站名称
    private String waterUserName;//水机用户姓名
    private String waterUserPhone;//水机用户手机号
    private Integer deviceId;//设备ID
    private String deviceModel;//设备型号
    private String deviceAddress;//设备所在地址
    private Date deviceInstallationTime;//设备安装时间
    private String snCode;//设备SN码
    private Boolean pay;//是否支付
    private Date payTime;//支付时间
    private Integer payType;//支付类型：1-微信；2-支付宝；3-POS机；4-转账；
    private String payTypeName;//支付类型：1-微信；2-支付宝；3-POS机；4-转账；
    private String payCredential;//线下支付凭证
    private Date payCredentialSubmitTime;//支付凭证提交时间
    private Integer status;//支付状态：0-待支付，1-待审核，2-支付成功，3-支付失败
    private String statusName;//支付状态：0-待支付，1-待审核，2-支付成功，3-支付失败
    private Date createTime;//订单创建时间
    private Integer terminal;//订单来源：8、广告屏  9、总部业务系统
    private String terminalName;
    private Date checkTime;//续费工单审核时间
    private Date updateTime;//订单更新时间
    private Boolean isBilling;//是否开票
    private Boolean deleted;//是否删除

    public OrderRenew() {
    }

    /**
     * 用业务对象OrderRenewDTO初始化数据库对象OrderRenew。
     *
     * @param dto 业务对象
     */
    public OrderRenew(OrderRenewDTO dto) {
        this.id = dto.getId();
        this.workOrderId = dto.getWorkOrderId();
        this.mainOrderId = dto.getMainOrderId();
        this.orderId = dto.getOrderId();
        this.tradeNo = dto.getTradeNo();
        this.amountFee = dto.getAmountFee();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.times = dto.getTimes();
        this.productFirstCategoryId = dto.getProductFirstCategoryId();
        this.productFirstCategoryName = dto.getProductFirstCategoryName();
        this.productSecondCategoryId = dto.getProductSecondCategoryId();
        this.productSecondCategoryName = dto.getProductSecondCategoryName();
        this.productCategoryId = dto.getProductCategoryId();
        this.productCategoryName = dto.getProductCategoryName();
        this.productCompanyId = dto.getProductCompanyId();
        this.productCompanyName = dto.getProductCompanyName();
        this.costId = dto.getCostId();
        this.costType = dto.getCostType();
        this.costTypeName = dto.getCostTypeName();
        this.costName = dto.getCostName();
        this.lastCostId = dto.getLastCostId();
        this.lastCostType = dto.getLastCostType();
        this.lastCostTypeName = dto.getLastCostTypeName();
        this.lastCostName = dto.getLastCostName();
        this.distributorId = dto.getDistributorId();
        this.distributorType = dto.getDistributorType();
        this.distributorTypeName = dto.getDistributorTypeName();
        this.distributorAccount = dto.getDistributorAccount();
        this.distributorIdCard = dto.getDistributorIdCard();
        this.distributorName = dto.getDistributorName();
        this.distributorPhone = dto.getDistributorPhone();
        this.distributorProvince = dto.getDistributorProvince();
        this.distributorCity = dto.getDistributorCity();
        this.distributorRegion = dto.getDistributorRegion();
        this.distributorStationName = dto.getDistributorStationName();
        this.distributorRecommendName = dto.getDistributorRecommendName();
        this.distributorRecommendProvince = dto.getDistributorRecommendProvince();
        this.distributorRecommendCity = dto.getDistributorRecommendCity();
        this.distributorRecommendRegion = dto.getDistributorRecommendRegion();
        this.distributorRecommendStationName = dto.getDistributorRecommendStationName();
        this.engineerId = dto.getEngineerId();
        this.engineerName = dto.getEngineerName();
        this.engineerPhone = dto.getEngineerPhone();
        this.engineerStationName = dto.getEngineerStationName();
        this.waterUserName = dto.getWaterUserName();
        this.waterUserPhone = dto.getWaterUserPhone();
        this.deviceId = dto.getDeviceId();
        this.deviceModel = dto.getDeviceModel();
        this.deviceAddress = dto.getDeviceAddress();
        this.deviceInstallationTime = dto.getDeviceInstallationTime();
        this.snCode = dto.getSnCode();
        this.pay = dto.getPay();
        this.payTime = dto.getPayTime();
        this.payType = dto.getPayType();
        this.payTypeName = dto.getPayTypeName();
        this.payCredential = dto.getPayCredential();
        this.payCredentialSubmitTime = dto.getPayCredentialSubmitTime();
        this.status = dto.getStatus();
        this.statusName = dto.getStatusName();
        this.createTime = dto.getCreateTime();
        this.terminal = dto.getTerminal();
        this.terminalName = dto.getTerminalName();
        this.checkTime = dto.getCheckTime();
        this.updateTime = dto.getUpdateTime();
        this.isBilling = dto.getIsBilling();
        this.deleted = dto.getDeleted();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象OrderRenewDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(OrderRenewDTO dto) {
        dto.setId(this.id);
        dto.setWorkOrderId(this.workOrderId);
        dto.setMainOrderId(this.mainOrderId);
        dto.setOrderId(this.orderId);
        dto.setTradeNo(this.tradeNo);
        dto.setAmountFee(this.amountFee);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setTimes(this.times);
        dto.setProductFirstCategoryId(this.productFirstCategoryId);
        dto.setProductFirstCategoryName(this.productFirstCategoryName);
        dto.setProductSecondCategoryId(this.productSecondCategoryId);
        dto.setProductSecondCategoryName(this.productSecondCategoryName);
        dto.setProductCategoryId(this.productCategoryId);
        dto.setProductCategoryName(this.productCategoryName);
        dto.setProductCompanyId(this.productCompanyId);
        dto.setProductCompanyName(this.productCompanyName);
        dto.setCostId(this.costId);
        dto.setCostType(this.costType);
        dto.setCostTypeName(this.costTypeName);
        dto.setCostName(this.costName);
        dto.setLastCostId(this.lastCostId);
        dto.setLastCostType(this.lastCostType);
        dto.setLastCostTypeName(this.lastCostTypeName);
        dto.setLastCostName(this.lastCostName);
        dto.setDistributorId(this.distributorId);
        dto.setDistributorType(this.distributorType);
        dto.setDistributorTypeName(this.distributorTypeName);
        dto.setDistributorAccount(this.distributorAccount);
        dto.setDistributorIdCard(this.distributorIdCard);
        dto.setDistributorName(this.distributorName);
        dto.setDistributorPhone(this.distributorPhone);
        dto.setDistributorProvince(this.distributorProvince);
        dto.setDistributorCity(this.distributorCity);
        dto.setDistributorRegion(this.distributorRegion);
        dto.setDistributorStationName(this.distributorStationName);
        dto.setDistributorRecommendName(this.distributorRecommendName);
        dto.setDistributorRecommendProvince(this.distributorRecommendProvince);
        dto.setDistributorRecommendCity(this.distributorRecommendCity);
        dto.setDistributorRecommendRegion(this.distributorRecommendRegion);
        dto.setDistributorRecommendStationName(this.distributorRecommendStationName);
        dto.setEngineerId(this.engineerId);
        dto.setEngineerName(this.engineerName);
        dto.setEngineerPhone(this.engineerPhone);
        dto.setEngineerStationName(this.engineerStationName);
        dto.setWaterUserName(this.waterUserName);
        dto.setWaterUserPhone(this.waterUserPhone);
        dto.setDeviceId(this.deviceId);
        dto.setDeviceModel(this.deviceModel);
        dto.setDeviceAddress(this.deviceAddress);
        dto.setDeviceInstallationTime(this.deviceInstallationTime);
        dto.setSnCode(this.snCode);
        dto.setPay(this.pay);
        dto.setPayTime(this.payTime);
        dto.setPayType(this.payType);
        dto.setPayTypeName(this.payTypeName);
        dto.setPayCredential(this.payCredential);
        dto.setPayCredentialSubmitTime(this.payCredentialSubmitTime);
        dto.setStatus(this.status);
        dto.setStatusName(this.statusName);
        dto.setCreateTime(this.createTime);
        dto.setTerminal(this.terminal);
        dto.setTerminalName(this.terminalName);
        dto.setCheckTime(this.checkTime);
        dto.setUpdateTime(this.updateTime);
        dto.setIsBilling(this.isBilling);
        dto.setDeleted(this.deleted);
    }
}