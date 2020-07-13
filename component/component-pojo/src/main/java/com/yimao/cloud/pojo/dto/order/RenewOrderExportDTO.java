package com.yimao.cloud.pojo.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author zhilin.he
 * @description 续费工单导出数据
 * @date 2019/5/8 10:21
 **/
@Getter
@Setter
public class RenewOrderExportDTO {

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
    private BigDecimal amountFee;//费用金额
    private Integer times;//第几次续费
    private String payTerminal;//8、广告屏  9、总部业务系统
    private String snCode;//设备SN码
    private String deviceModel;//设备型号
    private String userRealName;//用户姓名
    private String userPhone;//用户手机号
    private String isBilling;            //是否开票
    private String accountTime;             //结算时间
    private String distributorAccount;      //经销商登录名
    private String distributorIdCard;    //经销商身份证
    private String costCodeName;//套餐名称
    private String costType;  //扣费方式
    private BigDecimal modelPrice;//套餐金额


}
