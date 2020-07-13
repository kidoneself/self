package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.pojo.dto.user.RoleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 管理员角色
 *
 * @author Zhang Bo
 * @date 2018/11/3.
 */
@RestController
@Api(tags = "RoleController")
public class RoleController {

    @Resource
    private UserFeign userFeign;

    /**
     * 创建角色
     *
     * @param dto 角色信息
     * @return
     */
    @RequestMapping(value = "/role", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "创建角色")
    @ApiImplicitParam(name = "dto", value = "角色信息", dataType = "RoleDTO", paramType = "body", required = true)
    public void save(@RequestBody RoleDTO dto) {
        userFeign.saveRole(SystemType.SYSTEM.value, dto);
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return
     */
    @RequestMapping(value = "/role/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除角色")
    @ApiImplicitParam(name = "id", value = "角色ID", dataType = "Long", paramType = "path", required = true)
    public void delete(@PathVariable Integer id) {
        userFeign.deleteRole(id);
    }

    /**
     * 更新角色
     *
     * @param dto 角色信息
     * @return
     */
    @RequestMapping(value = "/role", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "更新角色")
    @ApiImplicitParam(name = "dto", value = "角色信息", required = true, dataType = "RoleDTO", paramType = "body")
    public void update(@RequestBody RoleDTO dto) {
        userFeign.updateRole(dto);
    }

    /**
     * 查询角色列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @return
     */
    @RequestMapping(value = "/roles/{pageNum}/{pageSize}", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询角色信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<RoleDTO> page(@PathVariable Integer pageNum,
                                @PathVariable Integer pageSize,
                                @RequestParam(required = false) String roleName) {
        return userFeign.getRolePageByParams(pageNum, pageSize, roleName, SystemType.SYSTEM.value);
    }

    /**
     * 根据角色ID获取所有菜单及权限集合
     *
     * @return
     */
    @RequestMapping(value = "/role/menus/permissions", method = RequestMethod.GET)
    @ApiOperation(value = "根据角色ID获取所有菜单及权限集合")
    @ApiImplicitParam(name = "roleId", value = "角色ID", dataType = "Long", paramType = "query")
    public List<MenuDTO> listMenuAndPermission(@RequestParam(required = false) Integer roleId) {
        return userFeign.listMenuAndPermission(SystemType.SYSTEM.value, roleId);
    }

}
