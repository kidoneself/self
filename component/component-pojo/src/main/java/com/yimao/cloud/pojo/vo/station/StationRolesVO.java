package com.yimao.cloud.pojo.vo.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "角色展示下拉列表类")
@Getter
@Setter
public class StationRolesVO {
	@ApiModelProperty(value = "ID")	
	private Integer id;
	@ApiModelProperty(value = "角色名称")
	private String roleName;
}
