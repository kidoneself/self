package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.pojo.dto.user.AdminDTO;
import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
import java.util.List;

/**
 * 管理员信息前端控制器
 *
 * @author Zhang Bo
 * @date 2018/10/30.
 */
@RestController
@Api(tags = "AdminController")
public class AdminController {

    @Resource
    private UserFeign userFeign;

    /**
     * 创建管理员
     *
     * @param dto 管理员信息
     */
    @PostMapping(value = "/admin")
    @ApiOperation(value = "创建管理员信息")
    @ApiImplicitParam(name = "dto", value = "管理员信息", required = true, dataType = "AdminDTO", paramType = "body")
    public void save(@RequestBody AdminDTO dto) {
        userFeign.saveAdmin(SystemType.SYSTEM.value, dto);
    }

    /**
     * 删除管理员账号
     *
     * @param id 管理员ID
     */
    @DeleteMapping(value = "/admin/{id}")
    @ApiOperation(value = "删除管理员账号")
    @ApiImplicitParam(name = "id", value = "管理员ID", required = true, dataType = "Long", paramType = "path")
    public void delete(@PathVariable Integer id) {
        userFeign.deleteAdmin(id);
    }

    /**
     * 禁用/启用管理员账号
     *
     * @param id 管理员ID
     */
    @PatchMapping(value = "/admin/{id}")
    @ApiOperation(value = "禁用管理员账号")
    @ApiImplicitParam(name = "id", value = "管理员ID", required = true, dataType = "Long", paramType = "path")
    public void forbidden(@PathVariable Integer id) {
        userFeign.forbiddenAdmin(id);
    }

    /**
     * 更新管理员
     *
     * @param dto 管理员信息
     */
    @PutMapping(value = "/admin")
    @ApiOperation(value = "更新管理员信息")
    @ApiImplicitParam(name = "dto", value = "管理员信息", required = true, dataType = "AdminDTO", paramType = "body")
    public void update(@RequestBody AdminDTO dto) {
        userFeign.updateAdmin(dto);
    }

    /**
     * 查询特定管理员
     *
     * @param id 管理员ID
     */
    @GetMapping(value = "/admin/{id}")
    @ApiOperation(value = "根据管理员ID查询管理员账号")
    @ApiImplicitParam(name = "id", value = "管理员ID", required = true, dataType = "Long", paramType = "path")
    public AdminDTO getById(@PathVariable Integer id) {
        return userFeign.getAdminDTOById(id);
    }

    /**
     * 查询管理员列表
     *
     * @param pageNum   第几页
     * @param pageSize  每页大小
     * @param roleId    角色ID
     * @param deptId    部门ID
     * @param realName  姓名
     * @param forbidden 禁用状态
     */
    @GetMapping(value = "/admins/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询所有管理员信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "roleId", value = "角色", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "deptId", value = "部门", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "realName", value = "姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "forbidden", value = "禁用", dataType = "Long", paramType = "query")
    })
    public PageVO<AdminDTO> listAdmin(@PathVariable Integer pageNum,
                                      @PathVariable Integer pageSize,
                                      @RequestParam(required = false) Integer roleId,
                                      @RequestParam(required = false) Integer deptId,
                                      @RequestParam(required = false) String realName,
                                      @RequestParam(required = false) Boolean forbidden) {
        return userFeign.getAdminPageByParams(pageNum, pageSize, SystemType.SYSTEM.value, roleId, deptId, realName, forbidden);
    }

    /**
     * 为管理员添加角色
     *
     * @param dto 管理员信息
     */
    @RequestMapping(value = "/admin/roles", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "为管理员添加角色")
    @ApiImplicitParam(name = "dto", value = "管理员信息", dataType = "AdminDTO", paramType = "body", required = true)
    public void addRoles(@RequestBody AdminDTO dto) {
        userFeign.addRolesToAdmin(dto);
    }

    /**
     * 获取管理员所拥有的菜单
     *
     * @param id 管理员ID
     */
    @RequestMapping(value = "/admin/{id}/menus", method = RequestMethod.GET)
    @ApiOperation(value = "获取管理员所拥有的菜单")
    @ApiImplicitParam(name = "id", value = "管理员ID", dataType = "Long", paramType = "path", required = true)
    public List<MenuDTO> listMenus(@PathVariable Integer id) {
        return userFeign.listMenusByAdminId(id);
    }

}
