package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Liu long jie
 * @description 订单查询条件
 * @date 2019-12-26
 **/
@Getter
@Setter
@ApiModel(description = "订单查询条件")
public class OrderQuery extends BaseQuery {

    @ApiModelProperty(value = "订单号")
    private Long idKey;
    @ApiModelProperty(value = "工单号")
    private String refer;
    @ApiModelProperty(value = "订单来源1-终端app；2-微信公众号；3-经销商APP；4-小程序；")
    private Integer terminal;
    @ApiModelProperty(value = "用户类型：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商\"")
    private Integer userType;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "产品类目id")
    private Integer productCategoryIdKey;
    @ApiModelProperty(value = " 订单状态:0-待付款 1-待审核 2-待发货 3-待出库  4-待收货 5-交易成功 6-售后中 7-交易关闭 8-已取消")
    private Integer status;
    @ApiModelProperty(value = "活动方式：1 普通产品， 2 折机商品，3-180产品  type 是租赁商品是有值")
    private Integer activityType;
    @ApiModelProperty(value = "收货人")
    private String receiver;
    @ApiModelProperty(value = "收货人联系方式")
    private String receiveMobile;
    @ApiModelProperty(value = "下单时间（始）", example = "2019-12-28 11:00:00")
    private Date beginTime;
    @ApiModelProperty(value = "下单时间（终）", example = "2019-12-28 11:00:00")
    private Date endTime;

    // ---------- 站务系统概况跳转到相关列表需传参数 ---------------
    @ApiModelProperty(value = "是否是有效产品销售订单")
    private Boolean isValidOrder;

}
