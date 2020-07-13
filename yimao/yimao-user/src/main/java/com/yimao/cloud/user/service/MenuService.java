package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.Menu;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/12/21.
 */
public interface MenuService {

    /**
     * 添加菜单
     *
     * @param menu 菜单信息
     */
    void save(Menu menu);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    void delete(Integer id);

    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     */
    void update(Menu menu);

    /**
     * 分页查询菜单信息
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param id       菜单ID
     * @return
     */
    PageVO<MenuDTO> page(Integer pageNum, Integer pageSize, Integer sysType, Integer id);

    /**
     * 获取级联菜单结构
     *
     * @return
     */
    List<MenuDTO> listMenuTree(Integer sysType);

    /**
     * @param pid      父级菜单ID
     * @param allMenus 所有菜单集合
     * @return
     */
    List<MenuDTO> getChildList(Integer pid, List<MenuDTO> allMenus);

    /**
     * 获取所有子菜单ID（包含自己）
     *
     * @param sysType 系统分类
     * @param menuId  父级菜单ID
     */
    List<Integer> getChildMenuIds(Integer sysType, Integer menuId);

    /**
     * 根据角色ID获取所有菜单及权限集合
     *
     * @param roleId 角色ID
     * @return
     */
    List<MenuDTO> listMenuAndPermission(Integer sysType, Integer roleId);
}
