package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
* 用户收益
*/
@Getter
@Setter
@ApiModel(description = "用户收益")
public class UserIncomeDTO {

    private Integer productTypeId; //产品类型ID
    private String productTypeName; //产品类型名称
    private BigDecimal income; //收益
    private Date balanceTime; //结算时间
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

    private Integer recordId; //分配主表ID
    private String subDistributorName; //子账号姓名
}
