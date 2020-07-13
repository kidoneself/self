package com.yimao.cloud.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DistributorIncomeDTO {

    private Integer productTypeId; //产品类型ID
    private String productTypeName; //产品类型名称
    private BigDecimal income; //收益
    private Date balanceTime; //支付时间
    private Integer userId; //e家号
    private String orderId; //订单号
    private Integer distributorId; // 经销商id
    private String resourceName; //用户来源名称
    private String nickName; //下单用户昵称
    private String headImg; //下单用户头像
    private Integer userSaleId; // 分销商id
    private BigDecimal userSaleMoney; //分销商收益
    private String userSaleNickName; //分销商昵称
    private String userSaleHeadImg; //分销商头像

}
