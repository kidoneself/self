package com.yimao.cloud.station.mapper;

import com.yimao.cloud.pojo.dto.station.StationPermissionCacheDTO;
import com.yimao.cloud.station.po.StationRolePermission;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

public interface StationRolePermissionMapper extends Mapper<StationRolePermission> {

    /**
     * 批量插入
     *
     * @param rolePermissionList
     */
    void batchInsert(@Param("list") List<StationRolePermission> rolePermissionList);

    List<StationPermissionCacheDTO> selectPermissionsByRoleId(Integer roleId);

    Set<Integer> selectPermissionIdsByRoleId(@Param("roleId") Integer roleId);

    List<Integer> selectRoleIdByPermissionId(@Param("permissionId") Integer permissionId);
}