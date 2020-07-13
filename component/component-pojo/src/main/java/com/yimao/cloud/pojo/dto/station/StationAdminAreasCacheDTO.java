package com.yimao.cloud.pojo.dto.station;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class StationAdminAreasCacheDTO {
	//绑定服务站门店id
	Set<Integer> stationIds;
	//售前服务站服务区域id
	Set<Integer> preAreaIds;
	//售后服务站服务区域id
	Set<Integer> afterAreaIds;
}
