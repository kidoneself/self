package com.yimao.cloud.pojo.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author zhilin.he
 * @description 工单发票导出数据
 * @date 2019/5/9 16:55
 **/
@Getter
@Setter
public class WorkOrderInvoiceExportDTO {

    private String workOrderId;          //工单号
    private String province;             //省
    private String city;                 //市
    private String region;               //区县
    private String userRealName;         //用户姓名
    private String userPhone;            //用户手机号
    private String engineerName;         //安装工姓名
    private String engineerPhone;        //安装工手机号
    private String deviceModel;          //商品类型
    private String costName;             //计费方式
    private String invoiceType;          //发票类型
    private String invoiceHead;          //发票抬头
    private String companyName;           //公司名称
    private String dutyNo;                //税号
    private String bankName;              //开户行
    private String bankAccount;           //开户号
    private String companyAddress;        //公司地址
    private String companyPhone;          //公司电话
    private String createTime;            //创建时间
    private String payTime;               //支付时间
    private String applyTime;             //开票时间
    private BigDecimal amountFee;         //开票金额
    private String confirmTime;           //确认时间

}
