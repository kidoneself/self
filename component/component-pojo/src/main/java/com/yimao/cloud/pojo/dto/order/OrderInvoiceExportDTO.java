package com.yimao.cloud.pojo.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 发票导出信息
 *
 * @author hhf
 * @date 2019/1/16 
 */
@Data
@ApiModel(description = "发票导出")
public class OrderInvoiceExportDTO implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1349561915385390091L;

	@ApiModelProperty(value = "工单号")
    private String workOrderId;

    @ApiModelProperty(value = "提货状态")
    private String isTake;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String region;

    @ApiModelProperty(value = "水机数量")
    private Integer count;

    @ApiModelProperty(value = "下单时间")
    private String orderTimeStr;

    @ApiModelProperty(value = "支付方式")
    private String payTypeStr;

    @ApiModelProperty(value = "状态")
    private String statusStr;

    @ApiModelProperty(value = "经销商姓名")
    private String realName;

    @ApiModelProperty(value = "经销商联系方式")
    private String distributorPhone;

    @ApiModelProperty(value = "经销商归属地")
    private String distributorAddress;

    @ApiModelProperty(value = "经销商服务站")
    private String distributorStation;

    @ApiModelProperty(value = "经销商推荐人")
    private String recommendName;

    @ApiModelProperty(value = "推荐人归属地")
    private String recommendAddress;

    @ApiModelProperty(value = "推荐人服务站")
    private String recommendStation;

    @ApiModelProperty(value = "客服姓名")
    private String serviceEngineerName;

    @ApiModelProperty(value = "客服联系方式")
    private String serviceEngineerPhone;

    @ApiModelProperty(value = "服务站")
    private String stationName;

    @ApiModelProperty(value = "客服接单时间")
    private String acceptTimeStr;

    @ApiModelProperty(value = "交易单号")
    private String tradeNo ;

    @ApiModelProperty(value = "计费方式")
    private String costName ;

    @ApiModelProperty(value = "计费金额")
    private BigDecimal modelPrice;

    @ApiModelProperty(value = "开户费")
    private BigDecimal openAccountFee;

    @ApiModelProperty(value = "商品类型")
    private String deviceModel;

    @ApiModelProperty(value = "SN码")
    private String snCode;

    @ApiModelProperty(value = "设备添加时间")
    private String deviceActiveTimeStr ;

    @ApiModelProperty(value = "支付时间")
    private String payTimeStr;

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "用户联系方式")
    private String userPhone;

    @ApiModelProperty(value = "派送方式")
    private String dispatchTypeStr;

    @ApiModelProperty(value = "支付端 1:经销商支付  2:其他(他人代付)  3:用户支付")
    private String payTerminalStr;

    @ApiModelProperty(value = "完成时间")
    private String completeTimeStr ;

    @ApiModelProperty(value = "提货时间")
    private String pickTimeStr;

    @ApiModelProperty(value = "物流编码")
    private String logisticsCode;

    @ApiModelProperty(value = "客户邮箱")
    private String billEmail;

    @ApiModelProperty(value = "是否开票")
    private String isBilling;

    @ApiModelProperty(value = "发票类型")
    private String invoiceTypeStr;

    @ApiModelProperty(value = "发票抬头")
    private String invoiceHeadStr;

    @ApiModelProperty(value = "税号")
    private String dutyNo;

    @ApiModelProperty(value = "开户行")
    private String bankName;

    @ApiModelProperty(value = "开户号")
    private String bankAccount;

    @ApiModelProperty(value = "开票金额")
    private BigDecimal billFee;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "地址")
    private String billAddress;

    @ApiModelProperty(value = "电话")
    private String billPhone;

    @ApiModelProperty(value = "开票时间")
    private String billTimeStr;

    @ApiModelProperty(value = "工单类型")
    private String workOrderTypeStr;

    @ApiModelProperty(value = "经销商类型")
    private String distributorTypeName;
}
