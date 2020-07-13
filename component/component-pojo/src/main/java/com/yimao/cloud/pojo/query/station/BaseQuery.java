package com.yimao.cloud.pojo.query.station;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class BaseQuery {
	private Integer stationCompanyId;
	private Set<Integer> areas;
	private Set<Integer> stations;
}
