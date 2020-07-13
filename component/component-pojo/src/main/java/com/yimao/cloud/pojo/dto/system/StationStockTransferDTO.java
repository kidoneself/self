package com.yimao.cloud.pojo.dto.system;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(description = "门店库存借调类")
public class StationStockTransferDTO implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = -5876556139096251699L;
	@ApiModelProperty(value = "调入服务站id")
	private Integer transferInStationId;
	
	@ApiModelProperty(value = "调出服务站id")
	private Integer transferOutStationId;
	
	@ApiModelProperty(value = "库存物资id")
    private Integer goodsMaterialsId;
	
	@ApiModelProperty(value = "调拨数")
	private Integer transferStockCount;
	
	@ApiModelProperty(value = "操作类型 0-服务站借调 1-后台调拨")
	private Integer type;
}
