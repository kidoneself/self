package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description: 订单配置
 * @author: yu chunlei
 * @create: 2019-08-08 15:56:56
 **/
@Data
@ApiModel(description = "订单配置")
public class OrderConfigDTO {

    @ApiModelProperty(value = "订单配置ID")
    private Integer id;
    @ApiModelProperty(value = "订单超时时间(小时)")
    private Integer orderTimeOut;
    @ApiModelProperty(value = "退货天数")
    private Integer returnDays;
    @ApiModelProperty(value = "发货天数")
    private Integer deliveryDays;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
