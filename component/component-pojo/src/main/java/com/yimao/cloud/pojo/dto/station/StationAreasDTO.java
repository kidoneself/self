package com.yimao.cloud.pojo.dto.station;

import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class StationAreasDTO {
	private Integer stationCompanyId;
	
	private Integer stationId;
	
	private Set<Integer> areas;

}
