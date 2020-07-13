package com.yimao.cloud.pojo.dto.hra;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class HraExportQuery implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String beginTime;
	private String endTime; 
	private Integer userSource; 
	private String mobile; 
	private String ticketNo; 
	private String province;  
	private String city; 
	private String region; 
	private String name; 
	private Integer reserveStatus;
	private Integer hasUpload;
	private Integer userId;
	private String ticketType;
	private List<Integer> stationIds;
	private Integer flag;

}
