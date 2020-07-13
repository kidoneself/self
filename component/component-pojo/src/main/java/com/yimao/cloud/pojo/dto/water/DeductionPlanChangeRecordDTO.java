package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：水机设备扣费计划修改记录
 *
 * @Author Zhang Bo
 * @Date 2019/12/04
 */
@ApiModel(description = "水机设备扣费计划修改记录DTO")
@Getter
@Setter
public class DeductionPlanChangeRecordDTO implements Serializable {

    private static final long serialVersionUID = -8403657481842440601L;

    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "设备ID")
    private Integer deviceId;
    @ApiModelProperty(position = 2, value = "操作内容")
    private String operation;
    @ApiModelProperty(position = 3, value = "操作人")
    private String creator;
    @ApiModelProperty(position = 4, value = "操作时间")
    private Date createTime;

}
