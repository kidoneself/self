package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.user.AdminDTO;
import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.pojo.dto.user.PermissionCacheDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.Admin;
import com.yimao.cloud.user.service.AdminService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * 管理员信息前端控制器
 *
 * @author Zhang Bo
 * @date 2018/10/30.
 */
@RestController
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 创建管理员
     *
     * @param dto 管理员信息
     */
    @PostMapping(value = "/admin")
    public void save(@RequestParam Integer sysType, @RequestBody AdminDTO dto) {
        Admin admin = new Admin(dto);
        admin.setSysType(sysType);
        adminService.save(admin, dto.getRoleIds());
    }

    /**
     * 删除管理员账号
     *
     * @param id 管理员ID
     */
    @DeleteMapping(value = "/admin/{id}")
    public void delete(@PathVariable Integer id,
                       HttpServletRequest request) {
        Admin admin = new Admin();
        admin.setId(id);
        adminService.delete(admin,request);
    }

    /**
     * 禁用/启用管理员账号
     *
     * @param id 管理员ID
     */
    @PatchMapping(value = "/admin/{id}")
    public void forbidden(@PathVariable Integer id) {
        Admin admin = new Admin();
        admin.setId(id);
        adminService.forbidden(admin);
    }

    /**
     * 更新管理员
     *
     * @param dto 管理员信息
     */
    @PutMapping(value = "/admin")
    public void update(@RequestBody AdminDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            Admin admin = new Admin(dto);
            adminService.update(admin, dto.getRoleIds());
        }
    }

    /**
     * 查询特定管理员
     *
     * @param id 管理员ID
     */
    @GetMapping(value = "/admin/{id}")
    public AdminDTO getById(@PathVariable Integer id) {
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return null;
        }
        AdminDTO dto = new AdminDTO();
        admin.convert(dto);
        return dto;
    }

    /**
     * 查询管理员列表
     *
     * @param pageNum   第几页
     * @param pageSize  每页大小
     * @param sysType   所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；
     * @param roleId    角色ID
     * @param deptId    部门ID
     * @param realName  管理员姓名
     * @param forbidden 禁用状态
     */
    @GetMapping(value = "/admins/{pageNum}/{pageSize}")
    public PageVO<AdminDTO> page(@PathVariable Integer pageNum,
                                 @PathVariable Integer pageSize,
                                 @RequestParam Integer sysType,
                                 @RequestParam(required = false) Integer roleId,
                                 @RequestParam(required = false) Integer deptId,
                                 @RequestParam(required = false) String realName,
                                 @RequestParam(required = false) Boolean forbidden) {
        return adminService.page(pageNum, pageSize, sysType, roleId, deptId, realName, forbidden);
    }

    /**
     * 为管理员添加角色
     *
     * @param dto 管理员信息
     */
    @RequestMapping(value = "/admin/roles", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addRoles(@RequestBody AdminDTO dto) {
        adminService.addRoles(dto.getId(), dto.getRoleIds());
    }

    /**
     * 获取管理员所拥有的菜单
     *
     * @param id 管理员ID
     */
    @RequestMapping(value = "/admin/{id}/menus", method = RequestMethod.GET)
    public List<MenuDTO> listMenus(@PathVariable Integer id) {
        return adminService.listMenusByAdminId(id);
    }

    /**
     * 获取管理员权限列表
     *
     * @param id 管理员ID
     */
    @GetMapping(value = "/admin/{id}/permissions")
    public Set<PermissionCacheDTO> listPermissions(@PathVariable Integer id) {
        return adminService.listPermissionsByAdminId(id);
    }

    // /**
    //  * 测试日期时间在Feign中传递
    //  *
    //  * @param date 时间
    //  * @return
    //  */
    // @GetMapping(value = "/test/date")
    // @ApiOperation(value = "测试日期时间在Feign中传递", notes = "测试日期时间在Feign中传递")
    // @ApiImplicitParam(name = "date", value = "时间", dataType = "String", paramType = "query")
    // public Object login(@RequestParam("date") Date date) {
    //     System.out.println("输入的时间：" + DateUtil.dateToString(date));
    //     System.out.println("输入的时间：" + date);
    //     return ResponseEntity.ok(date);
    // }

}
