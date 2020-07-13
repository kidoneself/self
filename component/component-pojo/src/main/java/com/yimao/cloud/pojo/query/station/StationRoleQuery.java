package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StationRoleQuery {
	@ApiModelProperty(value = "角色名")
	private String roleName;

	@ApiModelProperty(value = "角色描述")
	private String discription;

	@ApiModelProperty(value = "服务站公司id")
	private Integer stationCompanyId;

}
