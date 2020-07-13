package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.AdminDTO;
import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.pojo.dto.user.PermissionCacheDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.Admin;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * 管理员信息业务类
 *
 * @author Zhang Bo
 * @date 2018/10/30.
 */
public interface AdminService {

    /**
     * 管理员登录
     *
     * @param userName 用户名
     * @param password 密码
     * @param sysType  所属系统
     * @param request  HTTP请求
     */
    AdminDTO login(String userName, String password, Integer sysType, HttpServletRequest request);

    /**
     * 获取管理员信息，并设置相关额外属性
     *
     * @param adminId 管理员ID
     * @return
     */
    AdminDTO getFullAdminDTOById(Integer adminId);

    /**
     * 获取管理员信息，并设置相关额外属性
     *
     * @param admin 管理员信息
     * @return
     */
    AdminDTO getFullAdminDTOById(Admin admin);

    /**
     * 创建管理员
     *
     * @param admin   管理员信息
     * @param roleIds 角色ID集合
     */
    void save(Admin admin, Set<Integer> roleIds);

    /**
     * 删除管理员
     *
     * @param admin 管理员信息（仅有id）
     * @param request
     */
    void delete(Admin admin, HttpServletRequest request);

    /**
     * 禁用/启用管理员账号
     *
     * @param admin 管理员信息（仅有id）
     * @return
     */
    void forbidden(Admin admin);

    /**
     * 更新管理员账号
     *
     * @param admin   管理员信息
     * @param roleIds 角色ID集合
     */
    void update(Admin admin, Set<Integer> roleIds);

    /**
     * 根据ID查询管理员信息
     *
     * @param id 管理员ID
     * @return
     */
    Admin getById(Integer id);

    /**
     * 分页查询管理员信息，可带查询条件
     *
     * @param pageNum   第几页
     * @param pageSize  每页大小
     * @param sysType   所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；
     * @param roleId    角色ID
     * @param deptId    部门ID
     * @param realName  姓名
     * @param forbidden 禁用状态
     * @return
     */
    PageVO<AdminDTO> page(int pageNum, int pageSize, Integer sysType, Integer roleId, Integer deptId, String realName, Boolean forbidden);

    /**
     * 为管理员添加角色
     *
     * @param adminId 管理员ID
     * @param roleIds 角色ID集合
     */
    void addRoles(Integer adminId, Set<Integer> roleIds);

    /**
     * 获取管理员所拥有的菜单
     *
     * @param id 管理员ID
     * @return
     */
    List<MenuDTO> listMenusByAdminId(Integer id);

    /**
     * 获取管理员权限列表
     *
     * @param id 管理员ID
     */
    Set<PermissionCacheDTO> listPermissionsByAdminId(Integer id);
}
