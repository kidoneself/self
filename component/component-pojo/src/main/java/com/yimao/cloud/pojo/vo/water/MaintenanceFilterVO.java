package com.yimao.cloud.pojo.vo.water;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/***
 * 功能描述:pad端维护滤芯
 *
 * @auther: liu yi
 * @date: 2019/4/1 9:58
 */
@Getter
@Setter
@ApiModel(description = "维护工单")
public class MaintenanceFilterVO {
    @ApiModelProperty(value = "维修工单ID")
    private String orderId;
    @ApiModelProperty(value = "所需耗材名称")
    private String materielDetailName;
    @ApiModelProperty(value = "产品范围")
    private String deviceScope;

}
