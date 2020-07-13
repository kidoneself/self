package com.yimao.cloud.pojo.dto.station;

import java.util.Date;
import java.util.Set;

import lombok.Data;

@Data
public class SystemRoleDTO {
	private Integer systemRoleId;
    private Integer stationId;
    private String areaIds;
    private Integer adminId;
    //用于区域变更设置缓存查询key
    private Integer stationCompanyId;
}
