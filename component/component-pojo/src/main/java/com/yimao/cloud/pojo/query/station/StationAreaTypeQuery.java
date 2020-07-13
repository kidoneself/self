package com.yimao.cloud.pojo.query.station;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class StationAreaTypeQuery {
	private Integer stationId;
	
	private Set<Integer> areaIds;
}
