package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.pojo.dto.user.PermissionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.feign.UserFeign;
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
 * @author Zhang Bo
 * @date 2018/11/3.
 */
@RestController
@Api(tags = "MenuController")
public class MenuController {

    @Resource
    private UserFeign userFeign;

    /**
     * 添加菜单
     *
     * @param dto 角色信息
     * @return
     */
    @RequestMapping(value = "/menu", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "添加菜单")
    @ApiImplicitParam(name = "dto", value = "菜单信息", dataType = "MenuDTO", paramType = "body", required = true)
    public void save(@RequestBody MenuDTO dto) {
        userFeign.saveMenu(SystemType.WATER.value, dto);
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return
     */
    @RequestMapping(value = "/menu/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除菜单")
    @ApiImplicitParam(name = "id", value = "菜单ID", dataType = "Long", paramType = "path", required = true)
    public void delete(@PathVariable Integer id) {
        userFeign.deleteMenu(id);
    }

    /**
     * 更新菜单
     *
     * @param dto 菜单信息
     * @return
     */
    @RequestMapping(value = "/menu", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "更新菜单")
    @ApiImplicitParam(name = "dto", value = "菜单信息", required = true, dataType = "MenuDTO", paramType = "body")
    public void update(@RequestBody MenuDTO dto) {
        userFeign.updateMenu(dto);
    }

    /**
     * 查询菜单列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @return
     */
    @RequestMapping(value = "/menus/{pageNum}/{pageSize}", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询菜单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "菜单ID", dataType = "Long", paramType = "query")
    })
    public PageVO<MenuDTO> page(@PathVariable Integer pageNum,
                                @PathVariable Integer pageSize,
                                @RequestParam(required = false) Integer id) {
        return userFeign.getMenuPageByParams(pageNum, pageSize, SystemType.WATER.value, id);
    }

    /**
     * 获取级联菜单结构
     *
     * @return
     */
    @RequestMapping(value = "/menu/tree", method = RequestMethod.GET)
    @ApiOperation(value = "获取级联菜单结构")
    public List<MenuDTO> listMenuTree() {
        return userFeign.listMenuTree(SystemType.WATER.value);
    }

    /**
     * 根据菜单ID获取权限列表
     *
     * @return
     */
    @RequestMapping(value = "/menu/permissions", method = RequestMethod.GET)
    @ApiOperation(value = "根据菜单ID获取权限列表")
    @ApiImplicitParam(name = "menuId", value = "菜单ID", dataType = "Long", paramType = "query")
    public List<PermissionDTO> listPermissionByMenuId(@RequestParam(required = false) Integer menuId) {
        return userFeign.listPermissionByMenuId(menuId);
    }

}
