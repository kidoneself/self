package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * @description: 发货订单实体信息
 * @author: yu chunlei
 * @create: 2019-08-22 13:42:49
 **/
@Data
@ApiModel(description = "发货台账导出字段")
public class DeliveryLedgerExportDTO implements Serializable {

    @ApiModelProperty(value = "发货日期", example = "2018-12-28 11:00:00")
    private String deliveryTime;

    @ApiModelProperty(value = "订单号")
    private Long orderId;

    @ApiModelProperty(value = "服务站公司名称")
    private String stationCompanyName;

    @ApiModelProperty(value = "快递公司")
    private String logisticsCompany;

    @ApiModelProperty(value = "快递单号")
    private String logisticsNo;

    @ApiModelProperty(value = "收货人联系方式")
    private String addresseePhone;

    @ApiModelProperty(value = "收货人省")
    private String addresseeProvince;

    @ApiModelProperty(value = "收货人市")
    private String addresseeCity;

    @ApiModelProperty(value = "收货人区")
    private String addresseeRegion;

    @ApiModelProperty(value = "收货人街道")
    private String addresseeStreet;

    @ApiModelProperty(value = "收货人地址")
    private String addressee;

    @ApiModelProperty(value = "收货人")
    private String addresseeName;


    @ApiModelProperty(value = "发货件数")
    private Integer num;

    @ApiModelProperty(value = "数量/盒")
    private Integer boxNum;

    @ApiModelProperty(value = "重量")
    private Integer weigh;

    @ApiModelProperty(value = "寄付方式")
    private String payType;

    @ApiModelProperty(value = "快递费")
    private Integer logisticsFee;

    @ApiModelProperty(value = "备注")
    private String remark;

}
