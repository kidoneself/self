package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Liu long jie
 * @description 退货订单查询条件
 * @date 2019-12-26
 **/
@Getter
@Setter
public class SalesReturnOrderQuery extends BaseQuery {

    @ApiModelProperty(value = "订单号")
    private Long id;
    @ApiModelProperty(value = "订单来源")
    private Integer terminal;
    @ApiModelProperty(value = "用户类型")
    private Integer userType;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "用户收货地区")
    private String userTackAddress;
    @ApiModelProperty(value = "产品类目id")
    private Integer productCategoryId;
    @ApiModelProperty(value = "经销商id")
    private Integer distributorId;
    @ApiModelProperty(value = "收货人")
    private String receiver;
    @ApiModelProperty(value = "收货人手机号")
    private String receiveMobile;
    @ApiModelProperty(value = "申请日期（始）", example = "2019-12-28 11:00:00")
    private Date startApplyTime;
    @ApiModelProperty(value = "申请日期（终）", example = "2019-12-28 11:00:00")
    private Date endApplyTime;
    @ApiModelProperty(value = "处理状态")
    private Integer status;
}
