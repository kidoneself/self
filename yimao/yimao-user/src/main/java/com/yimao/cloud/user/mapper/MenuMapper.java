package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.user.po.Menu;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2018/11/2.
 */
public interface MenuMapper extends Mapper<Menu> {

    /**
     * 获取所有菜单列表
     */
    List<MenuDTO> selectAllMenus(@Param("sysType") Integer sysType);

    /**
     * 获取所有菜单ID
     */
    Set<Integer> selectAllMenuId(@Param("sysType") Integer sysType);

    /**
     * 获取一级菜单列表
     */
    List<MenuDTO> selectFirstLevelMenus(@Param("sysType") Integer sysType);

    /**
     * 获取菜单列表（带权限信息）
     */
    List<MenuDTO> selectMenuWithPermission(@Param("sysType") Integer sysType);

    /**
     * 获取子菜单列表
     */
    Page<MenuDTO> selectSubMenus(@Param("id") Integer id, @Param("sysType") Integer sysType);

    /**
     * 获取一共有几级菜单
     */
    Integer selectMaxMenuLevel(@Param("sysType") Integer sysType);

    /**
     * 根据权限ID集合获取菜单集合
     *
     * @param permissionIds 权限ID集合
     */
    Set<MenuDTO> selectMenuBypermissionIds(@Param("permissionIds") Set<Integer> permissionIds);

    /**
     * 根据id查询pid
     *
     * @param id 菜单ID
     */
    Integer selectPidById(@Param("id") Integer id);

    /**
     * 获取管理员所拥有的菜单
     *
     * @param adminId 管理员ID
     */
    List<MenuDTO> selectMenusByAdminId(@Param("adminId") Integer adminId);

}
