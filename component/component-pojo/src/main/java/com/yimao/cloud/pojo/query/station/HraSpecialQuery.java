package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 评估卡管理--F卡管理 查询条件
 * @author: Liu Long Jie
 * @create: 2020-1-14 13:54:50
 **/

@Data
public class HraSpecialQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -3133991235655434576L;

    @ApiModelProperty(value = "门店id")
    private Integer stationId;

    @ApiModelProperty(value = "体检卡号")
    private String ticketNo;

    @ApiModelProperty(value = "体检卡状态")
    private Integer ticketStatus;


}
