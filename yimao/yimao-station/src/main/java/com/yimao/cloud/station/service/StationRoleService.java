package com.yimao.cloud.station.service;

import java.util.List;

import com.yimao.cloud.pojo.dto.station.StationAdminAreasCacheDTO;
import com.yimao.cloud.pojo.dto.station.StationMenuDTO;
import com.yimao.cloud.pojo.dto.station.StationPermissionCacheDTO;
import com.yimao.cloud.pojo.dto.station.StationRoleDTO;
import com.yimao.cloud.pojo.dto.station.SystemRoleDTO;
import com.yimao.cloud.pojo.query.station.StationRoleQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.station.po.SystemRole;

public interface StationRoleService {

	List<StationRoleDTO> getRoleList(Integer stationCompanyId);

    void save(StationRoleDTO dto);

    void delete(Integer id);

    void update(StationRoleDTO dto);

    PageVO<StationRoleDTO> page(Integer pageNum, Integer pageSize, StationRoleQuery query);

	void deleteByAdminId(Integer adminId);

	void batchInsert(List<SystemRole> list);

	StationAdminAreasCacheDTO findStationsAndAreasByAdminId(Integer adminId);

    List<StationMenuDTO> getMenuListByRoleId(Integer roleId,Integer serviceType);

	List<StationPermissionCacheDTO> findPermissionsByRoleId(Integer roleId);

	boolean existRoleIdByStationCompanyId(Integer roleId, Integer stationCompanyId);

	List<SystemRoleDTO> findSystemRoleByStationId(Integer stationId);

	void updateSystemRole(SystemRole systemRole);

}
