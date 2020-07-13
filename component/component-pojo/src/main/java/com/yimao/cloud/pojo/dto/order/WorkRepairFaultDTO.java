package com.yimao.cloud.pojo.dto.order;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "维修工单DTO")
public class WorkRepairFaultDTO implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 651037879114435235L;

	@ApiModelProperty("维修故障id")
    private Integer id;
	@ApiModelProperty("维修故障名称")
    private String faultName;
}
