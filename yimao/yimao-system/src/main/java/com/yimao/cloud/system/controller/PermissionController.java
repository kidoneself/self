package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.pojo.dto.user.PermissionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Zhang Bo
 * @date 2018/11/3.
 */
@RestController
@Api(tags = "PermissionController")
public class PermissionController {

    @Resource
    private UserFeign userFeign;

    /**
     * 创建权限
     *
     * @param dto 权限信息
     * @return
     */
    @PostMapping(value = "/permission")
    @ApiOperation(value = "新增权限")
    @ApiImplicitParam(name = "dto", value = "权限信息", required = true, dataType = "PermissionDTO", paramType = "body")
    public void save(@RequestBody PermissionDTO dto) {
        userFeign.savePermission(SystemType.SYSTEM.value, dto);
    }

    /**
     * 删除权限账号
     *
     * @param id 权限ID
     * @return
     */
    @DeleteMapping(value = "/permission/{id}")
    @ApiOperation(value = "删除权限")
    @ApiImplicitParam(name = "id", value = "权限ID", required = true, dataType = "Long", paramType = "path")
    public void delete(@PathVariable Integer id) {
        userFeign.deletePermission(id);
    }

    /**
     * 更新权限
     *
     * @param dto 权限信息
     * @return
     */
    @PutMapping(value = "/permission")
    @ApiOperation(value = "更新权限")
    @ApiImplicitParam(name = "dto", value = "权限信息", required = true, dataType = "PermissionDTO", paramType = "body")
    public void update(@RequestBody PermissionDTO dto) {
        userFeign.updatePermission(dto);
    }

    /**
     * 查询权限列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param menuId   菜单ID
     * @return
     */
    @GetMapping(value = "/permissions/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询所有权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "menuId", value = "菜单ID", dataType = "Long", paramType = "query")
    })
    public PageVO<PermissionDTO> page(@PathVariable Integer pageNum,
                                      @PathVariable Integer pageSize,
                                      @RequestParam(required = false) Integer menuId) {
        return userFeign.getPermissionPageByParams(pageNum, pageSize, SystemType.SYSTEM.value, menuId);
    }

}
