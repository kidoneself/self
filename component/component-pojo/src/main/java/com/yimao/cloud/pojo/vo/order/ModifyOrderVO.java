package com.yimao.cloud.pojo.vo.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 后台订单列表-修改订单信息页面初始化参数
 */
@ApiModel(description = "后台订单列表-修改订单信息页面初始化参数")
@Getter
@Setter
public class ModifyOrderVO {

    @ApiModelProperty(position = 1, value = "子订单号")
    @JsonSerialize(using=ToStringSerializer.class)
    private Long orderId;
    @ApiModelProperty(position = 2, value = "产品ID")
    private Integer productId;
    @ApiModelProperty(position = 3, value = "产品名称")
    private String productName;
    @ApiModelProperty(position = 4, value = "产品类型名称")
    private String productCategoryName;
    @ApiModelProperty(position = 5, value = "可更换产品列表")
    private List<ProductDTO> productList;
    @ApiModelProperty(position = 6, value = "收货人姓名")
    private String addresseeName;
    @ApiModelProperty(position = 7, value = "收货人性别：1-男；2-女；")
    private Integer addresseeSex;
    @ApiModelProperty(position = 8, value = "收货人手机号")
    private String addresseePhone;
    @ApiModelProperty(position = 9, value = "收货地址省")
    private String addresseeProvince;
    @ApiModelProperty(position = 10, value = "收货地址市")
    private String addresseeCity;
    @ApiModelProperty(position = 11, value = "收货地址区")
    private String addresseeRegion;
    @ApiModelProperty(position = 12, value = "收货地址街道")
    private String addresseeStreet;

}