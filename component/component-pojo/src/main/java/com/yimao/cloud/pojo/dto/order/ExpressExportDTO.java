package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * @description: 快递信息导出实体类
 * @author: yu chunlei
 * @create: 2019-08-22 18:18:16
 **/
@Data
@ApiModel(description = "快递信息导出实体类")
public class ExpressExportDTO implements Serializable {

    @ApiModelProperty(value = "物流单号")
    private String logisticsNo;

    @ApiModelProperty(value = "订单号")
    private Long orderId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "产品数量")
    private Integer count;

    @ApiModelProperty(value = "发货件数")
    private Integer num;

    @ApiModelProperty(value = "重量")
    private Integer weigh;

    @ApiModelProperty(value = "寄付方式")
    private String payType;

    @ApiModelProperty(value = "快递费")
    private Integer logisticsFee;

    @ApiModelProperty(value = "邮寄公司")
    private String sendCompany;

    @ApiModelProperty(value = "邮寄人")
    private String sender;

    @ApiModelProperty(value = "邮寄人联系方式")
    private String senderPhone;

    @ApiModelProperty(value = "邮寄人地址")
    private String senderAddress;

    @ApiModelProperty(value = "收货公司")
    private String receiveCompany;

    @ApiModelProperty(value = "收件人省")
    private String addressProvince;

    @ApiModelProperty(value = "收件人市")
    private String addressCity;

    @ApiModelProperty(value = "收件人区")
    private String addressRegion;

    @ApiModelProperty(value = "收件人街道")
    private String addressStreet;

    @ApiModelProperty(value = "收货地址")
    private String address;

    @ApiModelProperty(value = "收件人姓名")
    private String addressName;

    @ApiModelProperty(value = "收件人手机号")
    private String addressPhone;

    @ApiModelProperty(value = "固话")
    private String phone;

    @ApiModelProperty(value = "备注")
    private String remark;





}
