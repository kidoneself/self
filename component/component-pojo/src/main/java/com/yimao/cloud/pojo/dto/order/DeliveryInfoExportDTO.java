package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 发货信息导出实体
 * @author: yu chunlei
 * @create: 2019-08-22 18:10:17
 **/
@Data
@ApiModel(description = "发货信息导出字段")
public class DeliveryInfoExportDTO implements Serializable {

    @ApiModelProperty(value = "订单号")
    private Long orderId;

    @ApiModelProperty(value = "产品型号")
    private String productCategoryName;

    @ApiModelProperty(value = "产品数量")
    private Integer count;

    @ApiModelProperty(value = "收货人省")
    private String addresseeProvince;

    @ApiModelProperty(value = "收货人市")
    private String addresseeCity;

    @ApiModelProperty(value = "收货人区")
    private String addresseeRegion;

    @ApiModelProperty(value = "收货人街道")
    private String addresseeStreet;

    @ApiModelProperty(value = "收货人")
    private String addresseeName;

    @ApiModelProperty(value = "联系方式")
    private String addresseePhone;

    @ApiModelProperty(value = "备注")
    private String stationCompanyName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "发货日期")
    private String deliveryTime;
    @ApiModelProperty(value = "快递公司")
    private String logisticsCompany;
    @ApiModelProperty(value = "快递单号")
    private String logisticsNo;
    @ApiModelProperty(value = "收货人地址")
    private String addressee;
    @ApiModelProperty(value = "数量/盒")
    private String boxNum;
    @ApiModelProperty(value = "重量")
    private String weigh;
    @ApiModelProperty(value = "快递费")
    private String logisticsFee;
    @ApiModelProperty(value = "寄付方式")
    private String payType;
}
