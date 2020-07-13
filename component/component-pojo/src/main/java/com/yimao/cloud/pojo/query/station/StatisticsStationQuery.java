package com.yimao.cloud.pojo.query.station;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @author yaoweijun
 *
 */
@Data
public class StatisticsStationQuery {
	
	@ApiModelProperty(value = "服务站门店id")
	private Integer id;
	@ApiModelProperty(value = "服务站门店名字")
	private String name;
	@ApiModelProperty(value = "服务站门店售前售后类型")
	private Integer serviceType;
	@ApiModelProperty(value = "区域集合")
	private Set<Integer> areas;
}
