package com.yimao.cloud.station.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.station.StationPermissionCacheDTO;
import com.yimao.cloud.pojo.dto.station.StationPermissionDTO;
import com.yimao.cloud.station.po.StationPermission;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

public interface StationPermissionMapper extends Mapper<StationPermission> {
    List<StationPermission> selectByQuery(StationPermission query);

    /**
     * 查询除本身外 url 以及 method 、menuId相同的数量（只用于修改逻辑判断）
     *
     * @param stationPermission
     * @return
     */
    int checkPermissionExist(StationPermission stationPermission);

    Page<StationPermissionDTO> selectBySubMenuIdList(@Param("subMenuIdList") List<Integer> subMenuIdList);

    List<StationPermissionCacheDTO> selectPermissionCacheAll();

    List<StationPermissionCacheDTO> selectCodeAndMethodByRoleId(@Param("roleId") Integer roleId);

}