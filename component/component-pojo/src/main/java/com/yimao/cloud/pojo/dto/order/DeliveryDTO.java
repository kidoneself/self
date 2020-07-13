package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Chen Hui Yang
 * @date 2018/12/26
 * 订单发货单
 */
@Data
@ApiModel(description = "订单发货单列表查询")
public class DeliveryDTO {

    @ApiModelProperty( value = "订单ID")
    private Long orderId;                       
    @ApiModelProperty( value = "物流单号")
    private String logisticsNo;                 
    @ApiModelProperty( value = "用户id")
    private String userId;
    @ApiModelProperty( value = "收货人名称")
    private String receiver;
    @ApiModelProperty( value = "发货开始时间",example = "2018-12-28 11:00:00")
    private Date beginTime;
    @ApiModelProperty( value = "发货结束时间",example = "2018-12-28 11:00:00")
    private Date endTime;
    @ApiModelProperty( value = "支付时间开始",example = "2018-12-28 11:00:00")
    private Date payBeginTime;
    @ApiModelProperty( value = "支付时间结束",example = "2018-12-28 11:00:00")
    private Date payEndTime;
    @ApiModelProperty( value = "出库时间开始",example = "2018-12-28 11:00:00")
    private Date deliveryBeginTime;
    @ApiModelProperty( value = "出库时间结束",example = "2018-12-28 11:00:00")
    private Date deliveryEndTime;
    @ApiModelProperty( value = "订单来源:1-终端app，2-微信公众号，3-经销商APP  4-小程序")
    private String terminal;
    @ApiModelProperty( value = "订单状态：0-待付款 1-待审核 2-待发货 3-待出库  4-待收货 5-交易成功 6-售后中 7-交易关闭 8-已取消")
    private Integer status;
    @ApiModelProperty(value = "产品类目id键")
    private Integer productCategoryIdKey;

    @ApiModelProperty(value = "收货地址省")
    private String addresseeProvince;

    @ApiModelProperty(value = "收货地址市")
    private String addresseeCity;

    @ApiModelProperty(value = "收货地址区")
    private String addresseeRegion;


}
