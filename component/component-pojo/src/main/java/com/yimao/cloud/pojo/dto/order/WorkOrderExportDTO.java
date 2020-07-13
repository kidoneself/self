package com.yimao.cloud.pojo.dto.order;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author zhilin.he
 * @description 工单导出数据
 * @date 2019/4/23 10:46
 **/
@Getter
@Setter
public class WorkOrderExportDTO {

    private String id;                 //工单号（区分带字母的维修、维护和新老工单）
    private String isTake;             //提货状态
    private String province;           //省
    private String city;               //市
    private String region;             //区县
    private Integer count;             //数量
    private String createTime;         //工单创建时间
    private String payType;            //支付方式
    private String status;             //工单状态
    private String distributorRealName;    //经销商姓名
    private String distributorPhone;   //经销商手机号
    private String distributorAddress;             //经销商地址
    private String distributorRegion;              //经销商所在服务站
    private String distributorRefereeName;         //经销商推荐人姓名
    private String distributorRefereeAddress;         //经销商推荐人所在地址
    private String distributorRefereeRegion;        //经销商推荐人所在服务站
    private String engineerName;         //安装工姓名
    private String engineerPhone;        //安装工手机号
    private String stationName;          //服务站名称
    private String acceptTime;           //安装工接单时间
    private String tradeNo;              //交易流水
    private String costName;             //计费方式名称
    private BigDecimal modelPrice;       //计费模板价格
    private BigDecimal openAccountFee;   //开户费
    private String deviceModel;          //设备型号
    private String snCode;               //SN码
    private String deviceActiveTime;     //设备激活时间
    private String payTime;              //支付时间
    private String userId;             //下单用户id
    private String userName;             //用户姓名
    private String userPhone;            //用户手机号
    private String userTypeName;            //用户身份类型
    private String dispatchType;         //派单方式
    private String payTerminal;          //支付端 1:经销商支付  2:其他(他人代付)  3:用户支付
    private String completeTime;         //安装完成时间
    private String pickTime;             //提货时间
    private String logisticsCode;        //物流编码(批次码)
    private String billEmail;            //开票邮箱
    private String isBilling;            //是否开票
    private String invoice;              //发票类型
    private String billType;             //发票抬头类型
    private String taxreceiptNum;        //税号
    private String bank;                 //开户行
    private String bankNum;              //开户号
    private BigDecimal billFee;          //开票金额
    private String companyName;          //公司名称
    private String billAddress;          //开票地址
    private String billPhone;            //开票电话
    private String billTime;             //开票时间
    private String distributorRoleName;  //经销商角色名称
    private String firstUpgradeTime;     //第一升级时间
    private String accountMonth;         //账号月份
    private String subDistributorRealName;   //子经销商姓名
    private String subDistributorName;   //子经销商登录名
    private String distributorIdCard;    //经销商身份证
    private String distributorName;      //经销商登录名
    private String type;                //工单安装类型
    private String productRangeName;     //产品范围名称
    private String completePay;          //是否完款
    private BigDecimal completeFirstPayMoney; //首付金额
    private BigDecimal payCompleteMoney;      //完款金额
    private String payCompleteTime;       //完款时间
    private String payCompleteTradeNo;    //完款交易单号
    private String payCompletePayType;    //完款支付类型
    private String serviceEngineerName;   //服务工程师姓名
    private String serviceEngineerPhone;  //服务工程师手机号
    @JsonSerialize(using=ToStringSerializer.class)
    private Long subOrderId;              //子订单号
    private String otherPayType;          //其他支付类型
    private String receiveName;			//收货人姓名
    private String receivePhone;		//收货人联系方式
    private String receiveProvice;		//收货人省
    private String receiveCity;			//收货人市
    private String receiveRegion;		//收货人区
    private String receiveAdress;		//收货地址
    private String firstCategoryName;   //产品类型(一级类目)
    private String twoCategoryName;     //产品类型(二级类目)
    private String threeCategoryName;   //产品型号(三级类目)
    private String costTypeName;        //计费类型
    private String distributorAccount;  //经销商账户
    private String distributorTypeName; //经销商类型名称
    private String distributorFirstUpgradetime;//体验版经销商第一次升级时间
    private String settlementTime;//YYYY-MM
	private Integer createUserId;//下单人id
	private String createUserName;//下单人姓名
	private String createUserPhone;//下单人手机号
	private String chargeBackStatusText;//退单状态
}
