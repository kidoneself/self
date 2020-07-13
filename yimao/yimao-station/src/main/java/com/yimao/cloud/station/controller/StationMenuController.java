package com.yimao.cloud.station.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.station.StationMenuDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.station.po.StationMenu;
import com.yimao.cloud.station.service.StationMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 站务管理系统菜单控制器
 *
 * @author Liu Long Jie
 * @date 2019/12/25.
 */
@RestController
@Api(tags = "StationMenuController")
public class StationMenuController {

    @Resource
    private StationMenuService stationMenuService;

    /**
     * 系统--菜单管理--添加菜单
     *
     * @param dto 菜单信息
     */
    @RequestMapping(value = "/stationMenu", method = RequestMethod.POST)
    @ApiOperation(value = "添加菜单", notes = "添加菜单")
    @ApiImplicitParam(name = "dto", value = "菜单信息", required = true, dataType = "StationMenuDTO", paramType = "body")
    public Object save(@RequestBody StationMenuDTO dto) {
        StationMenu stationMenu = new StationMenu(dto);
        stationMenuService.save(stationMenu);
        return ResponseEntity.noContent().build();
    }

    /**
     * 系统--菜单管理--删除菜单
     *
     * @param id 菜单ID
     */
    @RequestMapping(value = "/stationMenu/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除菜单", notes = "删除菜单")
    @ApiImplicitParam(name = "id", value = "菜单ID", required = true, dataType = "Long", paramType = "path")
    public Object delete(@PathVariable Integer id) {
        stationMenuService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 系统--菜单管理--更新菜单
     *
     * @param dto 部门信息
     */
    @RequestMapping(value = "/stationMenu", method = RequestMethod.PUT)
    @ApiOperation(value = "更新菜单", notes = "更新菜单")
    @ApiImplicitParam(name = "dto", value = "菜单信息", required = true, dataType = "StationMenuDTO", paramType = "body")
    public Object update(@RequestBody StationMenuDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            StationMenu menu = new StationMenu(dto);
            stationMenuService.update(menu);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 系统--菜单管理--查询菜单列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    @RequestMapping(value = "/stationMenu/{pageNum}/{pageSize}", method = RequestMethod.GET)
    @ApiOperation(value = "查询菜单列表", notes = "查询菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "菜单id", dataType = "Long", paramType = "query")
    })
    public PageVO<StationMenuDTO> page(@PathVariable Integer pageNum,
                                       @PathVariable Integer pageSize,
                                       @RequestParam(required = false) Integer id) {
        return stationMenuService.page(pageNum, pageSize, id);
    }

    /**
     * 系统--菜单管理--获取级联菜单结构
     */
    @RequestMapping(value = "/stationMenu/tree", method = RequestMethod.GET)
    @ApiOperation(value = "获取级联菜单结构", notes = "获取级联菜单结构")
    public List<StationMenuDTO> listMenuTree() {
        return stationMenuService.listMenuTree();
    }
}
