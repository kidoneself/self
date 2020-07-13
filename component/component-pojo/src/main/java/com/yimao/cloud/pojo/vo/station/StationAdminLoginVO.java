package com.yimao.cloud.pojo.vo.station;

import java.util.List;

import com.yimao.cloud.pojo.dto.station.StationMenuDTO;

import lombok.Data;

@Data
public class StationAdminLoginVO {
	private String realName;
	
	private Integer stationCompanyId;
	
	private String stationCompanyName;
	
	private String token;
	
	private List<StationMenuDTO> menus;
	
	public StationAdminLoginVO() {
		
	}

	public StationAdminLoginVO(String realName, Integer stationCompanyId, String stationCompanyName, String token, List<StationMenuDTO> menus) {

		this.realName = realName;
		this.stationCompanyId=stationCompanyId;
		this.stationCompanyName=stationCompanyName;
		this.token = token;
		this.menus = menus;
	}
	
	
}
