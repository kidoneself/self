package com.yimao.cloud.order.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 服务收益记录
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@Table(name = "service_income_record")
@Getter
@Setter
public class ServiceIncomeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long mainOrderId;               //主订单ID
    private Long orderId;                   //子订单ID
    private BigDecimal orderFee;            //订单金额
    private String ticketNo;                //体检卡号
    private String deviceId;                //HRA设备ID
    private Date serviceTime;               //服务时间（评估卡使用时间）

    private Integer productId;              //产品ID
    private String productName;             //产品名称
    private Integer productCompanyId;       //产品公司ID
    private String productCompanyName;      //产品公司名称
    private Integer productCategoryId;      //产品三级类目ID
    private String productCategoryName;     //产品三级类目名称
    private BigDecimal productPrice;        //产品价格

    private Integer userId;                 //用户ID
    private Integer userType;               //用户类型
    private String userTypeName;            //用户类型名称
    private String userName;                //用户名称
    private String userPhone;               //用户手机号

    private Integer distributorId;          //经销商ID
    private String distributorName;         //经销商姓名
    private String distributorTypeName;     //经销商类型名称
    private String distributorAccount;      //经销商账号
    private String distributorProvince;     //经销商省
    private String distributorCity;         //经销商市
    private String distributorRegion;       //经销商区县

    private Integer stationId;              //服务站id
    private String stationName;             //服务站名称
    private String stationProvince;         //服务站省
    private String stationCity;             //服务站市
    private String stationRegion;           //服务站区

    private Integer incomeRuleId;           //收益规则id
    private Integer incomeType;             //收益类型：1-产品收益，2-续费收益，3-服务收益，4-招商收益
    private Integer allotType;              //分配规则：1-按比例分配 2-按金额分配
    private BigDecimal settlementFee;       //结算金额

    private Date createTime;                //创建时间
    private Date updateTime;                //更新时间


}