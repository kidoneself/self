package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhilin.he
 * @description 续费订单信息
 * @date 2019/1/21 17:01
 **/
@Data
@ApiModel(description = "续费订单信息")
public class OrderRenewDTO implements Serializable {
    private static final long serialVersionUID = -7082778169392393868L;

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
    private Integer oldEngineerId;//安装工转让使用到
}
