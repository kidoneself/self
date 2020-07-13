package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @description: 查询实体类
 * @author: yu chunlei
 * @create: 2019-08-27 16:24:04
 **/
@Getter
@Setter
public class AfterSalesConditionDTO implements Serializable {

    private static final long serialVersionUID = 291826045149595742L;

    @ApiModelProperty(value = "1-全部；2-待审核；3-审核记录；")
    private Integer type;

    @ApiModelProperty(value = "售后单号")
    private String salesId;

    @ApiModelProperty(value = "子订单号")
    private String orderId;

    @ApiModelProperty(value = "订单来源")
    private String terminal;

    @ApiModelProperty(value = "售后申请开始时间")
    private String startTime;

    @ApiModelProperty(value = "售后申请结束时间")
    private String endTime;

    @ApiModelProperty(value = "售后状态")
    private String status;

    @ApiModelProperty(value = "商品类型")
    private Integer productType;

    @ApiModelProperty(value = "售后申请端:1-翼猫APP 2-公众号 3-翼猫业务系统")
    private Integer afterApplicationTerminal;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "操作状态:0、审核不通过，1、审核通过")
    private Integer operationStatus;
    
    @ApiModelProperty(value = "服务站查询区域id集合")
    private Set<Integer> areas;

}
