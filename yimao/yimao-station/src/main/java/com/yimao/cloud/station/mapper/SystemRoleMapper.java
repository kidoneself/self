package com.yimao.cloud.station.mapper;

import java.util.List;

import com.yimao.cloud.pojo.dto.station.SystemRoleDTO;
import com.yimao.cloud.station.po.SystemRole;
import tk.mybatis.mapper.common.Mapper;

public interface SystemRoleMapper extends Mapper<SystemRole> {

	int deleteByAdminId(Integer adminId);

	void batchInsert(List<SystemRole> list);

	List<SystemRole> selectAreasByAdminId(Integer adminId);

	List<SystemRoleDTO> selectSystemRoleByStationId(Integer stationId);
}