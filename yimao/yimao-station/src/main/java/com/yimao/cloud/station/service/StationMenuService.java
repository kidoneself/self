package com.yimao.cloud.station.service;

import com.yimao.cloud.pojo.dto.station.StationMenuDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.station.po.StationMenu;

import java.util.List;

/**
 * 站务管理系统菜单业务层
 *
 * @author Liu Long Jie
 * @date 2019/12/25.
 */
public interface StationMenuService {
    PageVO<StationMenuDTO> page(Integer pageNum, Integer pageSize, Integer id);

    List<StationMenuDTO> listMenuTree();

    List<StationMenuDTO> getChildList(Integer pid, List<StationMenuDTO> allMenus);

    void save(StationMenu stationMenu);

    void delete(Integer id);

    void update(StationMenu menu);

    /**
     * 获取所有子菜单ID（包含自己）
     *
     * @param menuId 父级菜单ID
     */
    List<Integer> getChildMenuIds(Integer menuId);

    /**
     * 根据角色id获取所有菜单
     *
     * @param roleId
     * @param permissionsType
     * @return
     */
    List<StationMenuDTO> getMenuListByRoleId(Integer roleId, Integer permissionsType);

    List<StationMenuDTO> listMenuAndPermission(Integer roleId);

}
