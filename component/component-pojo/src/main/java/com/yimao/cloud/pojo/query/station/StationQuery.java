package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Liu Long Jie
 * @date 2019/1/24
 */
@ApiModel(description = "服务站门店查询条件(服务站站务系统)")
@Getter
@Setter
public class StationQuery extends BaseQuery implements Serializable {


    @ApiModelProperty(value = "服务站门店ID")
    private Integer stationId;

    @ApiModelProperty(value = "站长或者承包人姓名")
    private String realName;

    @ApiModelProperty(value = "是否承包：0-未承包；1-已承包；")
    private Boolean contract;

    @ApiModelProperty(value = "是否上线：0-未上线；1-已上线；")
    private Integer online;

}
