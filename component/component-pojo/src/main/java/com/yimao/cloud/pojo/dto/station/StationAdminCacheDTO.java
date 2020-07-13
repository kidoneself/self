package com.yimao.cloud.pojo.dto.station;

import java.util.List;

import lombok.Data;

@Data
public class StationAdminCacheDTO {
	 private Integer id;
	
	 private String userName;
	 
	 private String realName;
	 
	 private Integer roleId;
	 //服务站用户售前售后属性
	 private Integer type;
	 
	 private Integer stationCompanyId;
	    
	 private String stationCompanyName;
}
