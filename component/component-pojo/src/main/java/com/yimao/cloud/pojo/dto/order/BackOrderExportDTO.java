package com.yimao.cloud.pojo.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author zhilin.he
 * @description 退单导出数据
 * @date 2019/4/26 10:12
 **/
@Getter
@Setter
public class BackOrderExportDTO {

    private String id;                 //工单号（区分带字母的维修、维护和新老工单）
    private String type;                //工单安装类型
    private String isTake;             //提货状态
    private String province;           //省
    private String city;               //市
    private String region;             //区县
    private Integer count;             //数量
    private String createTime;         //工单创建时间
    private String isPaid;             //是否支付
    private String payType;            //支付方式
    private String status;             //工单状态
    private String distributorRealName;    //经销商姓名
    private String distributorPhone;     //经销商手机号
    private String engineerName;         //安装工姓名
    private String engineerPhone;        //安装工手机号
    private String stationName;          //服务站名称
    private String tradeNo;              //交易流水
    private String costName;             //计费方式名称
    private BigDecimal modelPrice;       //计费模板价格
    private BigDecimal openAccountFee;   //开户费
    private String deviceModel;          //设备型号
    private String snCode;               //SN码
    private String payTime;              //支付时间
    private String userName;             //用户姓名
    private String userPhone;            //用户手机号
    private String passType;             //退单状态
    private String chargeBackRemark;        //退单备注
    private String chargeBackReason;        //退单原因
    private String chargeBackTime;         //退单时间
    private String applyChargeBackTime;    //退单申请时间
    private String agreeChargeBackTime;   //完成退单时间
    private String payTerminal;          //支付端 1:经销商支付  2:其他(他人代付)  3:用户支付
    private String chargeBackType;         //退单发起人类型
    private String chargeBackMoneyType;    //退款状态
    private String preRefundMoney;         //预退款金额
    private String preRefundMark;         //预退款备注
    private String chargeBackMoneyMethod;   //退款类型
    private String chargeBackMoneyTime;     //退款时间
    private String chargeBackTradeOrder;   //退款单号
    private String refundMoney;         //实际退款金额
    private String refundMark;        //实际退款备注
    private String pickTime;             //提货时间
    private String subDistributorRealName;   //子经销商姓名
    private String subDistributorName;   //子经销商登录名
    private String serviceEngineerName;   //服务工程师姓名
    private String serviceEngineerPhone;  //服务工程师手机号
    private Long subOrderId;              //子订单号
    private String otherPayType;          //其他支付类型

}
