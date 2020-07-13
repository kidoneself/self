package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 发货详情实体类
 * @author: yu chunlei
 * @create: 2019-08-26 15:46:30
 **/
@Data
@ApiModel(description = "发货记录详情")
public class DeliveryDetailInfoDTO implements Serializable {

    //订单信息
    @ApiModelProperty( value = "主订单ID")
    private Long mainOrderId;
    @ApiModelProperty( value = "子订单ID")
    private Long orderId;
    @ApiModelProperty( value = "产品图片")
    private String productImg;
    @ApiModelProperty( value = "产品一级类目")
    private String productOneCategoryName;
    @ApiModelProperty( value = "产品二级类目")
    private String productTwoCategoryName;
    @ApiModelProperty( value = "产品三级类目")
    private String productCategoryName;
    @ApiModelProperty( value = "产品数量")
    private Integer num;
    @ApiModelProperty( value = "1-终端app，2-微信公众号，3-经销商APP  4-小程序")
    private Integer terminal;
    @ApiModelProperty( value = "用户ID")
    private Integer userId;
    @ApiModelProperty( value = "服务站名称")
    private String stationName;

    //收货信息
    @ApiModelProperty( value = "收货人")
    private String addressName;
    @ApiModelProperty( value = "收货人联系方式")
    private String addresseePhone;
    @ApiModelProperty( value = "收货人省")
    private String addresseeProvince;
    @ApiModelProperty( value = "收货人市")
    private String addresseeCity;
    @ApiModelProperty( value = "收货人区")
    private String addresseeRegion;
    @ApiModelProperty( value = "收货人街道")
    private String addresseeStreet;

    //发货信息
    @ApiModelProperty( value = "快递公司")
    private String logisticsCompany;
    @ApiModelProperty( value = "快递单号")
    private String logisticsNo;
    @ApiModelProperty( value = "发货数量")
    private Integer deliveryNum;
    @ApiModelProperty( value = "发货数量/盒")
    private Integer boxNum;
    @ApiModelProperty( value = "重量")
    private Integer weigh;
    @ApiModelProperty( value = "寄付方式")
    private String payType;
    @ApiModelProperty( value = "快递费")
    private Integer logisticsFee;
    @ApiModelProperty( value = "备注")
    private String remark;
    @ApiModelProperty( value = "发货联系人")
    private String contact;
    @ApiModelProperty( value = "发货省")
    private String province;
    @ApiModelProperty( value = "发货市")
    private String city;
    @ApiModelProperty( value = "发货区")
    private String region;
    @ApiModelProperty( value = "发货街道")
    private String street;

    //操作记录
    @ApiModelProperty( value = "设置发货人")
    private String currentUser;
    @ApiModelProperty( value = "设置发货时间")
    private Date setShipTime;
    @ApiModelProperty( value = "出库时间")
    private Date deliveryTime;
    @ApiModelProperty( value = "出库人")
    private String deliveryPeople;

}
