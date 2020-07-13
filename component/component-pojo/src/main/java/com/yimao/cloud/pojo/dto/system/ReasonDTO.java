package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@ApiModel(description = "退单、中止工单原因")
@Getter
@Setter
public class ReasonDTO {

    @ApiModelProperty(position = 1, value = "ID")
    private Integer id;

    @ApiModelProperty(position = 2, value = "原因类型：1、中止工单；2、退单；3、删除工单；")
    private Integer type;

    @ApiModelProperty(position = 3, value = "原因")
    private String reason;

    @ApiModelProperty(position = 4, value = "原因序号")
    private Integer reasonNum;

    @ApiModelProperty(position = 5, value = "创建时间")
    private Date createTime;


}
