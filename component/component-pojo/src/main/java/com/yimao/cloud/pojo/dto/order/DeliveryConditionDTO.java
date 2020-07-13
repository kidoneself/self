package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 查询条件实体类
 * @author: yu chunlei
 * @create: 2019-08-26 18:16:45
 **/
@Data
@ApiModel(description = "查询条件实体类")
public class DeliveryConditionDTO implements Serializable {

    @ApiModelProperty(value = "订单ID")
    private String orderId;
    @ApiModelProperty(value = "快递单号")
    private String logisticsNo;
    @ApiModelProperty(value = "发货开始时间")
    private String startTime;
    @ApiModelProperty(value = "发货结束时间")
    private String endTime;
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    @ApiModelProperty(value = "收货人姓名")
    private String addreessName;
    @ApiModelProperty(value = "订单来源")
    private Integer terminal;
    @ApiModelProperty(value = "导出类别:1-发货台账登记导出 2-订单发货信息导出 3-快递导出")
    private Integer exportType;
}
