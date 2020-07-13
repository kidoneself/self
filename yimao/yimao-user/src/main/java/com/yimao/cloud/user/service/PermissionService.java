package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.PermissionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.Permission;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/12/20.
 */
public interface PermissionService {

    /**
     * 新增权限
     *
     * @param permission 权限信息
     */
    void save(Permission permission);

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    void delete(Integer id);

    /**
     * 更新权限
     *
     * @param permission 权限信息
     */
    void update(Permission permission);

    /**
     * 分页查询权限信息，可带查询条件
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param menuId   菜单ID
     * @return
     */
    PageVO<PermissionDTO> page(Integer pageNum, Integer pageSize, Integer sysType, Integer menuId);

    /**
     * 根据菜单ID获取权限列表
     *
     * @param menuId 菜单ID
     * @return
     */
    List<PermissionDTO> listByMenuId(Integer menuId);
}
