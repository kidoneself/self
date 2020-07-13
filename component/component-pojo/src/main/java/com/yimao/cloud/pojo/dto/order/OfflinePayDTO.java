package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Chen Hui Yang
 * @date 2019/1/8
 */
@Data
@ApiModel(description = "线下支付审核")
public class OfflinePayDTO implements Serializable {

    @ApiModelProperty(position = 1,value = "主订单ID集合")
    private List<Long> mainOrderIdList;
    @ApiModelProperty(position = 2,value = "审核状态")
    private String status;
    @ApiModelProperty(position = 3,value = "订单类型")
    private String orderType;
    @ApiModelProperty(position = 4,value = "原因")
    private String reason;

}
