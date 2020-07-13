package com.yimao.cloud.station.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.station.StationMenuDTO;
import com.yimao.cloud.station.po.StationMenu;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

public interface StationMenuMapper extends Mapper<StationMenu> {

    Page<StationMenuDTO> selectSubMenus(@Param("id") Integer id);

    List<StationMenuDTO> selectAllMenus();

    List<StationMenuDTO> selectFirstLevelMenus();

    int checkMenuNameAndUrlExist(@Param("id") Integer id, @Param("name") String name,@Param("url") String url);

    Set<StationMenuDTO> selectMenuByPermissionIds(@Param("permissionIds") Set<Integer> permissionIds);

    Integer selectPidById(@Param("id") Integer id);

    Set<Integer> selectAllMenuId();

    List<StationMenuDTO> selectMenuByRoleId(@Param("roleId") Integer roleId);

    /**
     * 获取菜单列表（带权限信息）
     */
    List<StationMenuDTO> selectMenuWithPermission();

    /**
     * 获取一共有几级菜单
     */
    Integer selectMaxMenuLevel();

    StationMenuDTO selectMenuById(@Param("menuId") Integer menuId);
}