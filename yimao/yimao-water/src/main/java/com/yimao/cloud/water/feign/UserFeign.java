package com.yimao.cloud.water.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.user.AdminDTO;
import com.yimao.cloud.pojo.dto.user.DepartmentDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.pojo.dto.user.PermissionCacheDTO;
import com.yimao.cloud.pojo.dto.user.PermissionDTO;
import com.yimao.cloud.pojo.dto.user.RoleDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * 描述：用户微服务的接口列表
 *
 * @author Zhang Bo
 * @date 2018/7/4.
 */
@FeignClient(name = Constant.MICROSERVICE_USER)
public interface UserFeign {

    /**
     * 管理员登录
     *
     * @param userName 用户名
     * @param password 密码
     * @param sysType  所属系统
     */
    @GetMapping(value = "/admin/login")
    AdminDTO login(@RequestParam("userName") String userName, @RequestParam("password") String password, @RequestParam("sysType") Integer sysType);

    /**
     * 新增管理员
     *
     * @param dto 管理员信息
     */
    @RequestMapping(value = "/admin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveAdmin(@RequestParam("sysType") Integer sysType, @RequestBody AdminDTO dto);

    /**
     * 删除管理员
     *
     * @param id 管理员ID
     */
    @RequestMapping(value = "/admin/{id}", method = RequestMethod.DELETE)
    void deleteAdmin(@PathVariable("id") Integer id);

    /**
     * 禁用管理员
     *
     * @param id 管理员ID
     */
    @RequestMapping(value = "/admin/{id}", method = RequestMethod.PATCH)
    void forbiddenAdmin(@PathVariable("id") Integer id);

    /**
     * 更新管理员
     *
     * @param dto 管理员信息
     */
    @RequestMapping(value = "/admin", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateAdmin(@RequestBody AdminDTO dto);

    /**
     * 根据管理员ID获取管理员信息
     *
     * @param id 管理员ID
     */
    @RequestMapping(value = "/admin/{id}", method = RequestMethod.GET)
    AdminDTO getAdminDTOById(@PathVariable("id") Integer id);

    /**
     * 获取管理员列表
     *
     * @param pageNum   第几页
     * @param pageSize  每页几条数
     * @param roleId    角色ID
     * @param deptId    部门ID
     * @param realName  姓名
     * @param forbidden 禁用状态
     */
    @RequestMapping(value = "/admins/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<AdminDTO> getAdminPageByParams(@PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize,
                                          @RequestParam(value = "sysType") Integer sysType,
                                          @RequestParam(value = "roleId", required = false) Integer roleId,
                                          @RequestParam(value = "deptId", required = false) Integer deptId,
                                          @RequestParam(value = "realName", required = false) String realName,
                                          @RequestParam(value = "forbidden", required = false) Boolean forbidden);

    /**
     * 为管理员添加角色
     *
     * @param dto 管理员信息
     */
    @RequestMapping(value = "/admin/roles", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void addRolesToAdmin(@RequestBody AdminDTO dto);

    /**
     * 获取管理员所拥有的菜单
     *
     * @param id 管理员ID
     */
    @RequestMapping(value = "/admin/{id}/menus", method = RequestMethod.GET)
    List<MenuDTO> listMenusByAdminId(@PathVariable("id") Integer id);

    /**
     * 获取管理员权限列表
     *
     * @param id 管理员ID
     */
    @RequestMapping(value = "/admin/{id}/permissions", method = RequestMethod.GET)
    Set<PermissionCacheDTO> listPermissionsByAdminId(@PathVariable("id") Integer id);

    /**
     * 新增角色
     *
     * @param dto 角色信息
     */
    @RequestMapping(value = "/role", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveRole(@RequestParam("sysType") Integer sysType, @RequestBody RoleDTO dto);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    @RequestMapping(value = "/role/{id}", method = RequestMethod.DELETE)
    void deleteRole(@PathVariable("id") Integer id);

    /**
     * 更新角色
     *
     * @param dto 角色信息
     */
    @RequestMapping(value = "/role", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateRole(@RequestBody RoleDTO dto);

    /**
     * 获取角色列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页几条数
     */
    @RequestMapping(value = "/roles/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<RoleDTO> getRolePageByParams(@PathVariable(value = "pageNum") Integer pageNum,
                                        @PathVariable(value = "pageSize") Integer pageSize,
                                        @RequestParam(value = "sysType") Integer sysType);

    /**
     * 添加菜单
     *
     * @param dto 菜单信息
     */
    @RequestMapping(value = "/menu", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveMenu(@RequestParam("sysType") Integer sysType, @RequestBody MenuDTO dto);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    @RequestMapping(value = "/menu/{id}", method = RequestMethod.DELETE)
    void deleteMenu(@PathVariable("id") Integer id);

    /**
     * 更新菜单
     *
     * @param dto 菜单信息
     */
    @RequestMapping(value = "/menu", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateMenu(@RequestBody MenuDTO dto);

    /**
     * 获取菜单列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页几条数
     */
    @RequestMapping(value = "/menus/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<MenuDTO> getMenuPageByParams(@PathVariable(value = "pageNum") Integer pageNum,
                                        @PathVariable(value = "pageSize") Integer pageSize,
                                        @RequestParam(value = "sysType") Integer sysType,
                                        @RequestParam(value = "id", required = false) Integer id);

    /**
     * 获取级联菜单结构
     */
    @RequestMapping(value = "/menu/tree", method = RequestMethod.GET)
    List<MenuDTO> listMenuTree(@RequestParam(value = "sysType") Integer sysType);

    /**
     * 根据菜单ID获取权限列表
     *
     * @param menuId 菜单ID
     */
    @RequestMapping(value = "/menu/permissions", method = RequestMethod.GET)
    List<PermissionDTO> listPermissionByMenuId(@RequestParam(value = "menuId", required = false) Integer menuId);

    /**
     * 根据角色ID获取所有菜单及权限集合
     *
     * @param roleId 角色ID
     */
    @RequestMapping(value = "/role/menus/permissions", method = RequestMethod.GET)
    List<MenuDTO> listMenuAndPermission(@RequestParam("sysType") Integer sysType, @RequestParam(value = "roleId", required = false) Integer roleId);

    /**
     * 新增权限
     *
     * @param dto 权限信息
     */
    @RequestMapping(value = "/permission", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void savePermission(@RequestParam("sysType") Integer sysType, @RequestBody PermissionDTO dto);

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    @RequestMapping(value = "/permission/{id}", method = RequestMethod.DELETE)
    void deletePermission(@PathVariable("id") Integer id);

    /**
     * 更新权限
     *
     * @param dto 权限信息
     */
    @RequestMapping(value = "/permission", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updatePermission(@RequestBody PermissionDTO dto);

    /**
     * 获取权限列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页几条数
     */
    @RequestMapping(value = "/permissions/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<PermissionDTO> getPermissionPageByParams(@PathVariable(value = "pageNum") Integer pageNum,
                                                    @PathVariable(value = "pageSize") Integer pageSize,
                                                    @RequestParam(value = "sysType") Integer sysType,
                                                    @RequestParam(value = "menuId", required = false) Integer menuId);

    /**
     * 获取部门列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页几条数
     * @param name     部门名称
     */
    @RequestMapping(value = "/depts/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<DepartmentDTO> getDepartmentPageByParams(@PathVariable(value = "pageNum") Integer pageNum,
                                                    @PathVariable(value = "pageSize") Integer pageSize,
                                                    @RequestParam(value = "name", required = false) String name,
                                                    @RequestParam(value = "sysType") Integer sysType);


    /**
     * 新增部门
     *
     * @param dto
     */
    @RequestMapping(value = "/dept", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void save(@RequestBody DepartmentDTO dto);

    /**
     * 删除部门
     *
     * @param id
     */
    @RequestMapping(value = "/dept/{id}", method = RequestMethod.DELETE)
    void delete(@PathVariable("id") Integer id);

    /**
     * 部门更新
     *
     * @param dto
     */
    @RequestMapping(value = "/dept", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void update(@RequestBody DepartmentDTO dto);


    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID
     */
    @GetMapping(value = "/user/{id}")
    UserDTO getUserDTOById(@PathVariable(value = "id") Integer id);

    /**
     * 根据安装工ID获取安装工信息
     *
     * @param id 安装工ID
     */
    @GetMapping(value = "/engineer/{id}")
    EngineerDTO getEngineerDTOById(@PathVariable(value = "id") Integer id);

    /**
     * 根据安装工mongo库中ID获取安装工信息
     *
     * @param oldId 安装工mongo库中ID
     */
    @GetMapping(value = "/engineer")
    EngineerDTO getEngineerByOldId(@RequestParam(value = "oldId") String oldId);

    /**
     * 根据经销商mongo库中ID获取经销商信息
     *
     * @param oldId 经销商mongo库中ID
     */
    @GetMapping(value = "/distributor")
    DistributorDTO getDistributorByOldId(@RequestParam(value = "oldId") String oldId);

    /**
     * 根据经销商ID查询经销商信息（消息推送时只需获取很少的几个字段）
     */
    @GetMapping(value = "/distributor/{id}/formsgpushinfo")
    DistributorDTO getDistributorBasicInfoByIdForMsgPushInfo(@PathVariable("id") Integer id);

    /**
     * 根据安装工ID获取安装工信息（消息推送时只需获取很少的几个字段）
     */
    @GetMapping(value = "/engineer/{id}/formsgpushinfo")
    EngineerDTO getEngineerBasicInfoByIdForMsgPushInfo(@PathVariable(value = "id") Integer id);

    /**
     * 根据经销商ID查询经销商（单表信息）
     *
     * @param id 经销商ID
     */
    @GetMapping(value = "/distributor/{id}")
    DistributorDTO getDistributorBasicInfoById(@PathVariable("id") Integer id);

    @GetMapping("/waterdeviceuser/{id}")
    WaterDeviceUserDTO getDeviceUserById(@PathVariable(value = "id") Integer id);

    @GetMapping("/waterdeviceuser/datamove")
    List<WaterDeviceUserDTO> listAllDeviceUserForDataMove();

    /**
     * @Author Liu Long Jie
     * @Description 根据areaId 获取经销商id
     * @Date 2020-2-13 15:13:25
     * @Param
     **/
    @PostMapping(value = "/distributor/ids/area", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<Integer> getDistributorIdsByAreaIds(@RequestBody Set<Integer> areaIds);

    /**
     * @Author Liu Long Jie
     * @Description 根据服务地区id 获取安装工id集合
     * @Date 2020-2-13 15:13:25
     * @Param
     **/
    @PostMapping(value = "/engineer/ids/areas",consumes = MediaType.APPLICATION_JSON_VALUE)
    List<Integer> getEngineerIdsByAreaIds(@RequestBody Set<Integer> areaIds);

}
