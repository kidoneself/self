package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.RoleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.Role;

import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2018/11/22.
 */
public interface RoleService {

    /**
     * 创建角色信息
     *
     * @param role          角色信息
     * @param permissionIds 权限ID集合
     * @return
     */
    void save(Role role, Set<Integer> permissionIds);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void delete(Integer id);

    /**
     * 更新角色
     *
     * @param role          角色信息
     * @param permissionIds 权限ID集合
     */
    void update(Role role, Set<Integer> permissionIds);

    /**
     * 分页查询角色信息
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @return
     */
    PageVO<RoleDTO> page(int pageNum, int pageSize, String roleName, Integer sysType);

}
