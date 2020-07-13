package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * 下单时所需要的订单信息
 *
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@Getter
@Setter
@ApiModel(description = "订单对象")
public class OrderDTO extends BaseOrder implements Serializable {

    private static final long serialVersionUID = 6104482570547856852L;

    @ApiModelProperty(position = 2, value = "订单类型：1-普通订单；2-续费订单（水机续费时传2，其它一律传1）")
    private Integer type;
    @ApiModelProperty(position = 3, value = "经销商下单类型：1-为自己下单；2-为客户下单（经销商为客户下单时传2，其它一律传1）")
    private Integer subType;
    @ApiModelProperty(position = 3, value = "下单地址类型：1-个人客户地址；2-企业客户地址")
    private Integer addressType;
    @ApiModelProperty(position = 8, value = "订单总运费")
    private BigDecimal logisticsFee;

    @ApiModelProperty(position = 30, value = "购物车元素集合")
    private Set<ShopCartDTO> shopCartSet;

    //评估券下单信息
    @ApiModelProperty(position = 50, value = "购物车元素集合")
    private String ticketNo;

}
