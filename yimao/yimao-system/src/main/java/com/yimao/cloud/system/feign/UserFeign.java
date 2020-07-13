package com.yimao.cloud.system.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.system.*;
import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.export.user.WaterDeviceUserExport;
import com.yimao.cloud.pojo.query.user.DistributorOrderQueryDTO;
import com.yimao.cloud.pojo.query.user.EngineerQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 描述：系统微服务调用用户微服务的接口列表
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
    @RequestMapping(value = "/admin/login", method = RequestMethod.GET)
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
     * 新增部门
     *
     * @param dto 部门信息
     */
    @RequestMapping(value = "/dept", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveDepartment(@RequestBody DepartmentDTO dto);

    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    @RequestMapping(value = "/dept/{id}", method = RequestMethod.DELETE)
    void deleteDepartment(@PathVariable("id") Integer id);

    /**
     * 更新部门
     *
     * @param dto 部门信息
     */
    @RequestMapping(value = "/dept", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateDepartment(@RequestBody DepartmentDTO dto);

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
                                        @RequestParam(value = "roleName", required = false) String roleName,
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
     * 获取权限列表
     *
     * @param sysType 系统类型
     */
    @RequestMapping(value = "/permissions", method = RequestMethod.GET)
    List<PermissionCacheDTO> listPermissionBySysType(@RequestParam(value = "sysType") Integer sysType);

    /**
     * 创建经销商
     *
     * @param dto 经销商信息
     * @author hhf
     * @date 2019/1/24
     */
    @RequestMapping(value = "/distributor", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveDistributor(@RequestBody DistributorDTO dto);

    /**
     * 更新经销商
     *
     * @param dto 经销商信息
     * @author hhf
     * @date 2019/1/24
     */
    @RequestMapping(value = "/distributor", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateDistributor(@RequestBody DistributorDTO dto);

    /**
     * 分页查询经销商信息
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param query    查询信息
     * @author hhf
     * @date 2019/1/24
     */
    @RequestMapping(value = "/distributor/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<DistributorDTO> pageQueryDistributor(@PathVariable(value = "pageNum") Integer pageNum,
                                                @PathVariable(value = "pageSize") Integer pageSize,
                                                @RequestBody DistributorQueryDTO query);

    /**
     * 删除经销商账号
     *
     * @param id 经销商ID
     * @author hhf
     * @date 2019/1/25
     */
    @RequestMapping(value = "/distributor/{id}", method = RequestMethod.DELETE)
    void deleteDistributor(@PathVariable("id") Integer id);

    /**
     * 禁用/启用经销商账号
     *
     * @param id 经销商ID
     * @author hhf
     * @date 2019/1/25
     */
    @RequestMapping(value = "/distributor/{id}", method = RequestMethod.PATCH)
    void forbidden(@PathVariable("id") Integer id);

    /**
     * 禁止/启用经销商下单
     *
     * @param id 经销商ID
     * @author hhf
     * @date 2018/12/18
     */
    @RequestMapping(value = "/distributor/order/{id}", method = RequestMethod.PATCH)
    void forbiddenOrder(@PathVariable("id") Integer id);

    /**
     * 根据经销商ID查询经销商详情
     *
     * @param id 经销商Id
     * @author hhf
     * @date 2019/1/25
     */
    @RequestMapping(value = "/distributor/{id}/expansion", method = RequestMethod.GET)
    DistributorDTO getExpansionInfoById(@PathVariable("id") Integer id);

    /**
     * 经销商信息转让
     *
     * @param oriDistributorId 经销商Id
     * @param dto              经销商信息
     * @author hhf
     * @date 2019/1/25
     */
    @RequestMapping(value = "/distributor/transfer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void transferDistributor(@RequestParam("oriDistributorId") Integer oriDistributorId,
                             @RequestBody TransferDistributorDTO transferDTO);

    /**
     * 根据经销商账号查询经销商是否存在
     *
     * @param account 经销商账号
     * @author hhf
     * @date 2019/1/25
     */
    @RequestMapping(value = "/distributor/verify/{account}", method = RequestMethod.GET)
    Boolean existDistributorAccount(@PathVariable("account") String account);

    /**
     * 根据经销商ID查询经销商详情
     *
     * @param id 经销商Id
     */
    @RequestMapping(value = "/distributor/{id}", method = RequestMethod.GET)
    DistributorDTO getDistributorById(@PathVariable("id") Integer id);


    /**
     * @param pageSize
     * @param query
     * @Description: 用户列表
     * @author ycl
     * @param: * @param pageNum
     * @Create: 2019/3/7 15:46
     */
    @RequestMapping(value = "/user/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<UserDTO> pageQueryUser(@PathVariable(value = "pageNum") Integer pageNum,
                                  @PathVariable(value = "pageSize") Integer pageSize,
                                  @RequestBody UserContidionDTO query);

    /**
     * @Description: 用户列表-解绑手机号
     * @author ycl
     * @param: userId
     * @Create: 2019/3/7 16:02
     */
    @RequestMapping(value = "/user/unBindPhone/{userId}", method = RequestMethod.PATCH)
    void unBindPhone(@PathVariable("userId") Integer userId);


    /**
     * @Description: 用户信息变更记录
     * @author ycl
     * @param: userId
     * @Create: 2019/3/7 16:04
     */
    @RequestMapping(value = "/user/record/{userId}", method = RequestMethod.GET)
    UserChangeRecordListDTO getUserChangeRecord(@PathVariable("userId") Integer userId);

    /**
     * 用户概况
     *
     * @author hhf
     * @date 2019/3/19
     */
    @RequestMapping(value = "/user/overview", method = RequestMethod.GET)
    UserOverviewDTO overview();

    /**
     * 待办事项统计（企业信息审核，支付审核）
     *
     * @return ResponseEntity
     * @author hhf
     * @date 2019/3/23
     */
    @RequestMapping(value = "/distributor/overview", method = RequestMethod.GET)
    Map<String, Long> distributorOrderOverview();

    /**
     * 经营概况（用户相关）
     *
     * @param
     * @return BusinessProfileDTO
     * @author hhf
     * @date 2019/3/27
     */
    @RequestMapping(value = "/user/overview/business", method = RequestMethod.GET)
    BusinessProfileDTO overviewBusiness();

    /**
     * @Description: 用户信息 编辑
     * @author ycl
     * @param: dto
     * @Return: void
     * @Create: 2019/3/28 9:13
     */
    @RequestMapping(value = "/user", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateUser(@RequestBody UserDTO dto);

    /**
     * 新增经销商角色配置
     *
     * @param dto 经销商角色配置
     */
    @PostMapping(value = "/distributor/role", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveDistributorRole(@RequestBody DistributorRoleDTO dto);

    /**
     * 禁用/启用经销商角色配置
     *
     * @param id 经销商角色ID
     */
    @PatchMapping(value = "/distributor/role/{id}")
    void forbiddenDistributorRole(@PathVariable("id") Integer id);

    /**
     * 修改经销商角色配置
     *
     * @param dto 经销商角色配置
     */
    @PutMapping(value = "/distributor/role", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateDistributorRole(@RequestBody DistributorRoleDTO dto);

    /**
     * 分页查询经销商角色配置信息
     *
     * @param pageNum  页数
     * @param pageSize 每页大小
     */
    @GetMapping(value = "/distributor/roles/{pageNum}/{pageSize}")
    PageVO<DistributorRoleDTO> pageDistributorRole(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize);

    /**
     * 查询经销商角色配置信息（所有）
     */
    @GetMapping(value = "/distributor/roles")
    List<DistributorRoleDTO> listAllDistributorRole();

    /**
     * 验证代理排名的值是否存在
     *
     * @param agentLevel 代理类型1-省代；2-市代；4-区代
     * @param ranking    排名
     * @author hhf
     * @date 2019/4/3
     */
    @RequestMapping(value = "/distributor/rank", method = RequestMethod.GET)
    Boolean checkAgentRanking(@RequestParam("agentLevel") Integer agentLevel, @RequestParam("ranking") Integer ranking);

    /**
     * 根据省市区获取推荐人信息
     *
     * @param province 省
     * @param city     市
     * @param region   区
     * @author hhf
     * @date 2019/4/3
     */
    @GetMapping(value = "/distributor/recommend/{province}/{city}/{region}")
    List<DistributorDTO> getRecommendByAddress(@PathVariable(value = "province") String province,
                                               @PathVariable(value = "city") String city,
                                               @PathVariable(value = "region") String region);

    @GetMapping(value = "/user")
    List<UserDTO> getUserByUserName(@RequestParam(value = "username") String username);

    @RequestMapping(value = "/distributor/station", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<DistributorDTO> getDistributorByParams(@RequestBody StationDistributorQueryDTO query);

    /**
     * 普通用户下单的经销商信息
     *
     * @return UserIncomeAccountDTO
     * @author liuhao@yimaokeji.com
     */
    @RequestMapping(value = "/income/account", method = RequestMethod.GET)
    UserIncomeAccountDTO getIncomeAccount();

    /**
     * 经销商导出
     *
     * @author hhf
     * @date 2019/5/17
     */
    @RequestMapping(value = "/distributor/export", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<DistributorExportDTO> distributorExport(@RequestBody DistributorQueryDTO query);

    @RequestMapping(value = "/user/distributor/{id}", method = RequestMethod.GET)
    UserDTO getUserInfo(@PathVariable("id") Integer id);

    @GetMapping(value = "/user/{id}/single")
    UserDTO getBasicUserById(@PathVariable("id") Integer id);

    @RequestMapping(value = "/engineer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveEngineer(@RequestBody EngineerDTO dto);

    @RequestMapping(value = "/engineer", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateEngineer(@RequestBody EngineerDTO dto);

    @RequestMapping(value = "/engineer/{id}", method = RequestMethod.PATCH)
    void forbiddenEngineer(@PathVariable("id") Integer id);

    @RequestMapping(value = "/engineer/{id}/unbind", method = RequestMethod.PATCH)
    void unbindIccid(@PathVariable("id") String id,
                     @RequestParam("type") Integer type);

    @RequestMapping(value = "/engineer/{id}/binding", method = RequestMethod.PATCH)
    void bindingIccid(@PathVariable("id") Integer id, @RequestParam(value = "iccid") String iccid);

    @RequestMapping(value = "/engineer/{id}/detail", method = RequestMethod.GET)
    EngineerDTO getEngineerDetailById(@PathVariable("id") Integer id);

    @RequestMapping(value = "/engineers/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<EngineerDTO> pageEngineer(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize,
                                     @RequestBody EngineerQuery query);


    @RequestMapping(value = "/user/bind/wechat", method = RequestMethod.GET)
    Object unBindWechat(@RequestParam(value = "userId") Integer userId);


    @RequestMapping(value = "/user/change/ambassador", method = RequestMethod.GET)
    void changeAmbassador(@RequestParam(value = "userId") Integer userId,
                          @RequestParam(value = "ambassadorId") Integer ambassadorId);

    /**
     * @param [pageSize, query]
     * @return com.yimao.cloud.pojo.vo.PageVO<DistributorOrderDTO>
     * @description 分页查询经销商订单
     * @author Liu Yi
     * @date 9:34 2019/8/20
     **/
    @PostMapping(value = "distributor/order/{pageSize}/{pageNum}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<DistributorOrderDTO> distributorOrderPage(@PathVariable(value = "pageNum") Integer pageNum,
                                                     @PathVariable(value = "pageSize") Integer pageSize,
                                                     @RequestBody DistributorOrderQueryDTO query);

    /**
     * 经销商订单详情
     *
     * @param orderId
     */
    @GetMapping(value = "distributor/order/{orderId}")
    DistributorOrderAllInfoDTO findDistributorOrderAllInfoById(@PathVariable(value = "orderId") Long orderId);

    /**
     * @param
     * @return java.lang.Object
     * @description 导出经销商订单
     * @author Liu Yi
     * @date 2019/8/26 9:57
     */
    @GetMapping(value = "distributor/order/export", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "导出经销商订单")
    @ApiImplicitParam(name = "orderQuery", value = "订单查询实体", dataType = "DistributorOrderQueryDTO", paramType = "body")
    Object listExport(@RequestBody DistributorOrderQueryDTO orderQuery);
//    List<DistributorOrderExportDTO> listExport(@RequestBody DistributorOrderQueryDTO orderQuery);


    /**
     * 财务审核（批量）
     *
     * @param orderIds
     * @param activityStatus
     * @return
     */
    @PatchMapping(value = "financial/audit/batch")
    @ApiOperation(value = "财务审核（批量）", notes = "财务审核（批量）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderIds", value = "财务审核订单id", required = true, dataType = "Long", allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "activityStatus", value = "审核状态 1-通过 ; 2-不通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "string", paramType = "query")
    })
    Void auditBatch(@RequestParam("orderIds") List<Long> orderIds,
                    @RequestParam("activityStatus") Integer activityStatus,
                    @RequestParam(value = "cause", required = false) String cause);

    /**
     * 企业审核（批量）
     *
     * @param ids            企业审核主键
     * @param activityStatus 1-通过 ; 2-不通过
     * @param cause          审核不通过原因
     * @return java.lang.Object
     * @author hhf
     * @date 2018/12/18
     */
    @PatchMapping(value = "user/companyApply/audit/batch")
    @ApiOperation(value = "企业审核（批量）", notes = "企业审核（批量）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "企业审核ID", required = true, dataType = "Long", allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "activityStatus", value = "审核状态 1-通过 ; 2-不通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "String", paramType = "query")
    })
    Void CompanyAuditBatch(@RequestParam(value = "ids") List<Long> ids,
                           @RequestParam(value = "activityStatus") Integer activityStatus,
                           @RequestParam(value = "cause", required = false) String cause
    );

    /**
     * 审核记录导出
     *
     * @param distributorOrderId
     * @param orderType
     * @param distributorAccount
     * @param roleId
     * @param auditType
     * @param status
     * @return
     * @author Liu long jie
     * @date 2019/8/28
     */
    @PostMapping(value = "distributor/audit/export")
    @ApiOperation(value = "审核记录导出")
    @ApiImplicitParams({@ApiImplicitParam(name = "distributorOrderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorAccount", value = "经销商账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "原经销商类型Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "auditType", value = "审核类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "审核状态", dataType = "Long", paramType = "query")}
    )
    List<DistributorOrderAuditRecordExportDTO> exportDistributorOrderAuditRecordAudit(
            @RequestParam(value = "distributorOrderId", required = false) Long distributorOrderId,
            @RequestParam(value = "orderType", required = false) Integer orderType,
            @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
            @RequestParam(value = "roleId", required = false) Integer roleId,
            @RequestParam(value = "auditType", required = false) Integer auditType,
            @RequestParam(value = "status", required = false) Integer status
    );

    @GetMapping(value = "/user/companyApply/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询企业审核", notes = "分页查询企业审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "companyName", value = "企业名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "account", value = "经销商账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleLevel", value = "经销商类型", dataType = "Long", paramType = "query")
    })
    PageVO<UserCompanyApplyDTO> pageCompanyAudit(@PathVariable(value = "pageNum") Integer pageNum,
                                                 @PathVariable(value = "pageSize") Integer pageSize,
                                                 @RequestParam(value = "orderId", required = false) Long orderId,
                                                 @RequestParam(value = "orderType", required = false) Integer orderType,
                                                 @RequestParam(value = "companyName", required = false) String companyName,
                                                 @RequestParam(value = "account", required = false) String account,
                                                 @RequestParam(value = "roleLevel", required = false) Integer roleLevel
    );

    /**
     * 财务审核分页   可以带条件
     *
     * @param distributorOrderId
     * @param orderType
     * @param name
     * @param distributorAccount
     * @param roleId
     * @param destRoleId
     * @param payType
     * @param payStartTime
     * @param payEndTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "distributor/financial/{pageNum}/{pageSize}")
    @ApiOperation(value = "财务审核分页", notes = "财务审核分页")
    @ApiImplicitParams({@ApiImplicitParam(name = "distributorOrderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "经销商姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "distributorAccount", value = "经销商账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "原经销商类型Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "destRoleId", value = "变更后经销商类型Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "payType", value = "支付方式", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "payStartTime", value = "支付开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "payEndTime", value = "支付结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    PageVO<FinancialAuditDTO> pageFinancialAudit(@RequestParam(value = "distributorOrderId", required = false) Long distributorOrderId,
                                                 @RequestParam(value = "orderType", required = false) Integer orderType,
                                                 @RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
                                                 @RequestParam(value = "roleId", required = false) Integer roleId,
                                                 @RequestParam(value = "destRoleId", required = false) Integer destRoleId,
                                                 @RequestParam(value = "payType", required = false) Integer payType,
                                                 @RequestParam(value = "payStartTime", required = false) String payStartTime,
                                                 @RequestParam(value = "payEndTime", required = false) String payEndTime,
                                                 @PathVariable(value = "pageNum") Integer pageNum,
                                                 @PathVariable(value = "pageSize") Integer pageSize
    );

    /**
     * 审核记录分页
     *
     * @param pageNum
     * @param pageSize
     * @param distributorOrderId
     * @param orderType
     * @param distributorAccount
     * @param roleId
     * @param auditType
     * @param status
     * @return
     */
    @GetMapping(value = "distributor/audit/{pageNum}/{pageSize}")
    @ApiOperation(value = "审核记录分页")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "distributorOrderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorAccount", value = "经销商账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "原经销商类型Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "auditType", value = "审核类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "审核状态", dataType = "Long", paramType = "query")
    })
    PageVO<DistributorOrderAuditRecordDTO> pageDistributorAuditRecord(@PathVariable(value = "pageNum") Integer pageNum,
                                                                      @PathVariable(value = "pageSize") Integer pageSize,
                                                                      @RequestParam(value = "distributorOrderId", required = false) Long distributorOrderId,
                                                                      @RequestParam(value = "orderType", required = false) Integer orderType,
                                                                      @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
                                                                      @RequestParam(value = "roleId", required = false) Integer roleId,
                                                                      @RequestParam(value = "auditType", required = false) Integer auditType,
                                                                      @RequestParam(value = "status", required = false) Integer status);

    /**
     * 财务审核   状态修改
     *
     * @param orderId
     * @param financialState
     * @param cause
     * @return
     */
    @PatchMapping(value = "distributor/financial/{orderId}")
    @ApiOperation(value = "财务审核 ", notes = "财务审核")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderId", value = "订单号", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "financialState", value = "财务审核状态", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "string", paramType = "query")
    })
    Void financialAudit(@PathVariable(value = "orderId") Long orderId,
                        @RequestParam(value = "financialState") Integer financialState,
                        @RequestParam(value = "cause", required = false) String cause);


    /**
     * 企业审核
     *
     * @param id             企业审核主键
     * @param activityStatus 1-通过 ; 2-不通过
     * @param cause          审核不通过原因
     * @author liulongjie
     * @date 2019/8/29
     */
    @PatchMapping(value = "/user/companyApply/audit/{id}")
    @ApiOperation(value = "企业审核", notes = "企业审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "企业审核ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "activityStatus", value = "审核状态 1-通过 ; 2-不通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "String", paramType = "query")
    })
    Void companyAudit(@PathVariable(value = "id") Long id,
                      @RequestParam(value = "activityStatus") Integer activityStatus,
                      @RequestParam(value = "cause", required = false) String cause);

    @GetMapping(value = "/distributor/changeInfo")
    UserChangeRecordListDTO getChangeInfoByUserId(@RequestParam(value = "userId") Integer userId);

    /***
     * 根据区域Id获取未禁用的安装工信息
     * @param areaId
     * @return
     */
    @GetMapping(value = "/engineers/area")
    public List<EngineerDTO> listEngineerByRegion(@RequestParam(value = "areaId") Integer areaId);


    /**
     * 查看合同页
     *
     * @param distributorOrderId
     * @return
     */
    @GetMapping(value = "/distributor/protocol/view/{distributorOrderId}")
    String previewDistributorProtocol(@PathVariable(value = "distributorOrderId") Long distributorOrderId);

    @PutMapping(value = "/distributor/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateDistributorCommon(@RequestBody DistributorDTO dto);

    @RequestMapping(value = "/waterdeviceuser/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<WaterDeviceUserDTO> pageQueryCustomer(@PathVariable(value = "pageNum") Integer pageNum,
                                                 @PathVariable(value = "pageSize") Integer pageSize,
                                                 @RequestBody(required = false) CustomerContidionDTO query);


    @GetMapping("/waterdeviceuser/{id}")
    WaterDeviceUserDTO getDeviceUserDTOInfo(@PathVariable(value = "id") Integer id);


    @RequestMapping(value = "waterdeviceuser", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateDeviceUserInfo(@RequestBody WaterDeviceUserDTO deviceUserDTO);

    //客户列表导出
    @RequestMapping(value = "/waterdeviceuser/export", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<WaterDeviceUserExport> customersExportList(@RequestBody CustomerContidionDTO query);

    /**
     * 根据安装工ID获取安装工信息（消息推送时只需获取很少的几个字段）
     */
    @GetMapping(value = "/engineer/{id}/formsgpushinfo")
    EngineerDTO getEngineerBasicInfoByIdForMsgPushInfo(@PathVariable(value = "id") Integer id);

    @GetMapping(value = "/user/engineerlist/station/{id}")
    List<EngineerDTO> getEngineerListByEngineerId(@PathVariable(value = "id") Integer id);

    /***
     * 安装工转让
     * @param oldId
     * @param newId
     */
    @RequestMapping(value = "/engineer/transfer/{oldId}", method = RequestMethod.PATCH)
    void transferEngineer(@PathVariable(value = "oldId") Integer oldId, @RequestParam(value = "newId", required = false) Integer newId);

    /**
     * 将指定服务区域下的安装工转给指定服务站门店
     *
     * @param transferAreaInfo 转让区域及承包方信息
     */
    @PostMapping(value = "/engineer/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
    void transferEngineerToNewStationByServiceArea(@RequestBody TransferAreaInfoDTO transferAreaInfo);

    /**
     * 将原服务于指定服务区域的安装工对该区域的服务权限删除，给指定安装工新增对该地区的服务权限
     *
     * @param transferAreaInfo 转让区域及承包方信息
     */
    @PostMapping(value = "/engineer/updateServiceArea", consumes = MediaType.APPLICATION_JSON_VALUE)
    void engineerUpdateServiceArea(@RequestBody TransferAreaInfoDTO transferAreaInfo);

    /**
     * 根据服务站id获取该服务站下的所有安装工
     *
     * @return
     */
    @GetMapping(value = "/user/engineerlist/byStationId/{stationId}")
    List<EngineerDTO> getEngineerListByStationId(@PathVariable(value = "stationId") Integer stationId);

    /**
     * 修改绑定指定服务站门店的安装工的服务区域
     *
     * @param updateEngineerServiceAreaDataInfo
     */
    @PutMapping(value = "/user/engineer/updateEngineerServiceArea", consumes = MediaType.APPLICATION_JSON_VALUE)
    Void updateEngineerServiceArea(@RequestBody UpdateEngineerServiceAreaDataInfo updateEngineerServiceAreaDataInfo);
}
