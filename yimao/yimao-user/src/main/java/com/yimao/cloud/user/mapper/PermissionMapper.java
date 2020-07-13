package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.user.PermissionCacheDTO;
import com.yimao.cloud.pojo.dto.user.PermissionDTO;
import com.yimao.cloud.user.po.Permission;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/2.
 */
public interface PermissionMapper extends Mapper<Permission> {

    /**
     * 根据角色ID获取角色拥有的权限列表
     *
     * @param roleId 角色ID
     */
    List<PermissionCacheDTO> selectCodeAndMethodByRoleId(@Param("roleId") Integer roleId);

    /**
     * 根据菜单ID获取权限列表
     *
     * @param menuId 菜单ID
     */
    Page<PermissionDTO> selectPermissionByMenuId(@Param("menuId") Integer menuId);

    /**
     * 根据系统类型获取权限列表
     *
     * @param sysType 系统类型
     */
    List<PermissionCacheDTO> selectBySysType(@Param("sysType") Integer sysType);
}
