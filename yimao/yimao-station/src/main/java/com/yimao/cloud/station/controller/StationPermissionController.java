package com.yimao.cloud.station.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.station.StationPermissionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.station.po.StationPermission;
import com.yimao.cloud.station.service.StationPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 站务管理权限管理控制器
 *
 * @author Liu Long Jie
 * @date 2019/12/25.
 */
@RestController
@Api(tags = "StationPermissionController")
public class StationPermissionController {

    @Resource
    private StationPermissionService stationPermissionService;

    /**
     * 系统--权限管理--创建权限
     *
     * @param dto 权限信息
     */
    @PostMapping(value = "/stationPermission")
    @ApiOperation(value = "创建权限", notes = "创建权限")
    @ApiImplicitParam(name = "dto", value = "权限信息", required = true, dataType = "StationPermissionDTO", paramType = "body")
    public Object save(@RequestBody StationPermissionDTO dto) {
        StationPermission permission = new StationPermission(dto);
        stationPermissionService.save(permission);
        return ResponseEntity.noContent().build();
    }

    /**
     * 系统--权限管理--删除权限账号
     *
     * @param id 权限ID
     */
    @DeleteMapping(value = "/stationPermission/{id}")
    @ApiOperation(value = "删除权限账号", notes = "删除权限账号")
    @ApiImplicitParam(name = "id", value = "权限ID", required = true, dataType = "Long", paramType = "path")
    public Object delete(@PathVariable Integer id) {
        stationPermissionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 系统--权限管理--更新权限
     *
     * @param dto 权限信息
     */
    @PutMapping(value = "/stationPermission")
    @ApiOperation(value = "更新权限", notes = "更新权限")
    @ApiImplicitParam(name = "dto", value = "权限信息", required = true, dataType = "StationPermissionDTO", paramType = "body")
    public Object update(@RequestBody StationPermissionDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            StationPermission permission = new StationPermission(dto);
            stationPermissionService.update(permission);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 系统--权限管理--查询权限列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param menuId   菜单ID
     */
    @GetMapping(value = "/stationPermission/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询权限列表", notes = "查询权限列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "菜单id", dataType = "Long", paramType = "query")
    })
    public PageVO<StationPermissionDTO> page(@PathVariable Integer pageNum,
                                             @PathVariable Integer pageSize,
                                             @RequestParam(required = false) Integer menuId) {
        return stationPermissionService.page(pageNum, pageSize, menuId);
    }

    /**
     * 系统--权限管理--批量新增权限
     *
     * @param dtoList 权限信息
     */
    @PostMapping(value = "/stationPermission/batch")
    @ApiOperation(value = "批量新增权限", notes = "批量新增权限")
    @ApiImplicitParam(name = "dtoList", value = "权限信息", required = true, dataType = "List", paramType = "body")
    public void batchSave(@RequestBody List<StationPermissionDTO> dtoList) {
        stationPermissionService.batchSave(dtoList);
    }
}
