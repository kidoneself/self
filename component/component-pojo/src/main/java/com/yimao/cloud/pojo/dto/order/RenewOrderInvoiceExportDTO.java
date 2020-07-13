package com.yimao.cloud.pojo.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhilin.he
 * @description 续费发票导出数据
 * @date 2019/5/9 14:16
 **/
@Getter
@Setter
public class RenewOrderInvoiceExportDTO {

    private String renewId;//续费单号
    private String workOrderId;//工单号
    private String province;           //省
    private String city;               //市
    private String region;             //区县
    private String renewTime;//续费时间
    private String payType;//支付类型：1-微信；2-支付宝；3-POS机；4-转账；
    private String payTime;//支付时间
    private String distributorRealName;//经销商名称
    private String distributorPhone;//经销商电话
    private String distributorAddress;//经销商归属地
    private String distributorRegion;//经销商服务站
    private String distributorRefereeName;         //经销商推荐人姓名
    private String distributorRefereeAddress;         //经销商推荐人所在地址
    private String distributorRefereeRegion;        //经销商推荐人所在服务站
    private String engineerName;         //安装工姓名
    private String engineerPhone;        //安装工手机号
    private String stationName;          //服务站名称
    private String tradeNo;//订单支付单号，第三方支付流水号
    private String lastCostName;//上一次计费方式
    private String costName;  //计费方式
    private BigDecimal amountFee;//续费金额
    private String snCode;//设备SN码
    private String userRealName;//用户姓名
    private String userPhone;//用户手机号
    private String applyStatus;           //开票状态
    private String applyTime;             //开票时间
    private String invoiceType;          //发票类型
    private String invoiceHead;          //发票抬头
    private String companyName;           //公司名称
    private String companyAddress;        //公司地址
    private String companyPhone;          //公司电话
    private String bankName;              //开户行
    private String bankAccount;           //开户号
    private String dutyNo;                //税号
    private String confirmTime;             //确认时间

}
