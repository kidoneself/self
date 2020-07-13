package com.yimao.cloud.station.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.station.StationRoleDTO;
import com.yimao.cloud.pojo.query.station.StationRoleQuery;
import com.yimao.cloud.station.po.StationRole;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface StationRoleMapper extends Mapper<StationRole> {

    List<StationRoleDTO> selectRolesByStationCompanyId(@Param("stationCompanyId")Integer stationCompanyId);
    														   
    int selectCountByRoleName(@Param("roleName") String roleName, @Param("stationCompanyId") Integer stationCompanyId);

    int selectCountByRoleNameAndId(@Param("roleName") String roleName, @Param("id") Integer id, @Param("stationCompanyId") Integer stationCompanyId);

    Page<StationRoleDTO> selectByRoleNameAndStationCompanyId(StationRoleQuery query);

	StationRole selectByQuery(StationRole role);
}