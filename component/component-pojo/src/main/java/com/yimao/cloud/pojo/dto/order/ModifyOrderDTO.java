package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 后台修改子订单操作确定按钮所需携带的参数
 */
@ApiModel(description = "后台修改子订单操作确定按钮所需携带的参数")
@Getter
@Setter
public class ModifyOrderDTO {

    @ApiModelProperty(position = 1, value = "子订单号")
    private Long orderId;
    @ApiModelProperty(position = 2, value = "产品ID")
    private Integer productId;
    @ApiModelProperty(position = 3, value = "收货人姓名")
    private String addresseeName;
    @ApiModelProperty(position = 4, value = "收货人性别：1-男；2-女；")
    private Integer addresseeSex;
    @ApiModelProperty(position = 5, value = "收货人手机号")
    private String addresseePhone;
    @ApiModelProperty(position = 6, value = "收货地址省")
    private String addresseeProvince;
    @ApiModelProperty(position = 7, value = "收货地址市")
    private String addresseeCity;
    @ApiModelProperty(position = 8, value = "收货地址区")
    private String addresseeRegion;
    @ApiModelProperty(position = 9, value = "收货地址街道")
    private String addresseeStreet;

    @ApiModelProperty(position = 10, value = "产品型号")
    private String categoryName;

    @ApiModelProperty(position = 11, value = "计费方式ID")
    private Integer costId;
    @ApiModelProperty(position = 12, value = "计费方式名称")
    private String costName;

}