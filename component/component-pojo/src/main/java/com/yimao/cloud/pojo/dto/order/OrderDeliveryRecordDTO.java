package com.yimao.cloud.pojo.dto.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 发货记录
 * @author: yu chunlei
 * @create: 2019-08-21 09:42:38
 **/
@Data
@ApiModel(description = "发货记录")
public class OrderDeliveryRecordDTO implements Serializable {

    @ApiModelProperty( value = "发货记录ID")
    private Integer id;
    @ApiModelProperty( value = "快递单号")
    private String logisticsNo;
    @ApiModelProperty( value = "快递公司")
    private String logisticsCompany;
    @ApiModelProperty( value = "子订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    @ApiModelProperty( value = "主订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long mainOrderId;
    @ApiModelProperty( value = "一级类目id")
    private Integer productOneCategoryId;
    @ApiModelProperty( value = "一级类目名称")
    private String productOneCategoryName;
    @ApiModelProperty( value = "二级类目id")
    private Integer productTwoCategoryId;
    @ApiModelProperty( value = "二级类目名称")
    private String productTwoCategoryName;
    @ApiModelProperty( value = "三级类目id")
    private Integer productCategoryId;
    @ApiModelProperty( value = "类目名称")
    private String productCategoryName;
    @ApiModelProperty( value = "产品图片")
    private String productImg;
    @ApiModelProperty( value = "发货件数")
    private Integer num;
    @ApiModelProperty( value = "产品数量")
    private Integer count;
    @ApiModelProperty( value = "发货数量/盒")
    private Integer boxNum;
    @ApiModelProperty( value = "发货时间",example = "2018-12-28 11:00:00")
    private Date deliveryTime;
    @ApiModelProperty( value = "来源端：1-健康e家公众号 2-经销商APP 3-净水设备")
    private Integer terminal;
    @ApiModelProperty( value = "用户id")
    private Integer userId;
    @ApiModelProperty( value = "收货人姓名")
    private String addressName;
    @ApiModelProperty( value = "收货人联系方式")
    private String addressPhone;
    @ApiModelProperty( value = "收货人省")
    private String addressProvince;
    @ApiModelProperty( value = "收货人市")
    private String addressCity;
    @ApiModelProperty( value = "收货人区")
    private String addressRegion;
    @ApiModelProperty( value = "收货人街道")
    private String addressStreet;
    @ApiModelProperty( value = "服务站id")
    private Integer stationId;
    @ApiModelProperty( value = "服务站名称")
    private String stationName;
    @ApiModelProperty( value = "寄件公司")
    private String sendingCompany;
    @ApiModelProperty( value = "寄件人")
    private String sender;
    @ApiModelProperty( value = "寄件人所属省")
    private String senderProvince;
    @ApiModelProperty( value = "寄件人所属市")
    private String senderCity;
    @ApiModelProperty( value = "寄件人所属区")
    private String senderRegion;
    @ApiModelProperty( value = "寄件人所属街道")
    private String senderStreet;
    @ApiModelProperty( value = "寄件人联系方式")
    private String senderPhone;
    @ApiModelProperty( value = "设置发货人")
    private String setShipper;
    @ApiModelProperty( value = "设置发货时间")
    private Date setShipTime;
    @ApiModelProperty( value = "出库人")
    private String deliveryPeople;
    @ApiModelProperty( value = "创建时间")
    private Date createTime;
    @ApiModelProperty( value = "修改时间")
    private Date updateTime;
    @ApiModelProperty( value = "寄付方式")
    private String payType;
    @ApiModelProperty( value = "重量")
    private Integer weigh;

    @ApiModelProperty( value = "运费")
    private Integer logisticsFee;
    @ApiModelProperty( value = "备注")
    private String remark;


}
