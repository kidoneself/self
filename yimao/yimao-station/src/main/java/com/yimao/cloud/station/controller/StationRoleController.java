package com.yimao.cloud.station.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.station.StationMenuDTO;
import com.yimao.cloud.pojo.dto.station.StationRoleDTO;
import com.yimao.cloud.pojo.query.station.StationRoleQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.StationRolesVO;
import com.yimao.cloud.station.constant.StationConstant;
import com.yimao.cloud.station.service.StationMenuService;
import com.yimao.cloud.station.service.StationRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@Api(tags = "StationRoleController")
@Slf4j
public class StationRoleController {

    @Resource
    private UserCache userCache;
    @Resource
    private StationRoleService roleService;
    @Resource
    private StationMenuService stationMenuService;

    /**
     * 系统-角色管理-创建角色
     *
     * @param dto 角色信息
     * @return
     */
    @RequestMapping(value = "/stationRole", method = RequestMethod.POST)
    @ApiOperation(value = "创建角色")
    @ApiImplicitParam(name = "dto", value = "角色信息", required = true, dataType = "StationRoleDTO", paramType = "body")
    public Object save(@RequestBody StationRoleDTO dto) {
        log.info("admin={}", JSON.toJSONString(dto));
        roleService.save(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 系统-角色管理-删除角色
     *
     * @param id 角色ID
     * @return
     */
    @RequestMapping(value = "/stationRole/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除菜单", notes = "删除菜单")
    @ApiImplicitParam(name = "id", value = "角色ID", required = true, dataType = "Long", paramType = "path")
    public Object delete(@PathVariable Integer id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 系统-角色管理-编辑角色
     *
     * @param dto 角色信息
     * @return
     */
    @RequestMapping(value = "/stationRole", method = RequestMethod.PUT)
    @ApiOperation(value = "更新角色", notes = "更新角色")
    @ApiImplicitParam(name = "dto", value = "角色信息", required = true, dataType = "StationRoleDTO", paramType = "body")
    public Object update(@RequestBody StationRoleDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            roleService.update(dto);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 系统-角色管理- 角色列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @return
     */
    @RequestMapping(value = "/stationRoles/{pageNum}/{pageSize}", method = RequestMethod.POST)
    @ApiOperation(value = "查询角色列表", notes = "查询角色列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StationRoleQuery", paramType = "body")
    })
    public Object page(@PathVariable Integer pageNum,
                       @PathVariable Integer pageSize,
                       @RequestBody StationRoleQuery query) {

        PageVO<StationRoleDTO> page = roleService.page(pageNum, pageSize, query);
        return ResponseEntity.ok(page);
    }

    /**
     * 系统-员工管理-新增/编辑-查看可用角色下拉列表
     */
    @GetMapping("/stationRole/roleList")
    @ApiOperation(value = "角色下拉列表")
    public List<StationRolesVO> getRoleList(Integer stationCompanyId) {

        if (Objects.isNull(stationCompanyId)) {
            throw new BadRequestException("未选择服务站公司");
        }

        Integer cacheStationCompanyId = userCache.getJWTInfo().getStationCompanyId();

        if (Objects.isNull(cacheStationCompanyId)) {
            throw new YimaoException("用户登录失效。");
        }

        if (cacheStationCompanyId.equals(StationConstant.SUPERCOMPANYID)) {
            cacheStationCompanyId = stationCompanyId;
        } else {

            if (!cacheStationCompanyId.equals(stationCompanyId)) {
                throw new YimaoException("选择服务站公司不属于该用户所在服务站公司");
            }
        }

        List<StationRolesVO> resultList = new ArrayList<StationRolesVO>();
        log.info("cacheStationCompanyId={}", cacheStationCompanyId);
        List<StationRoleDTO> stationRoleList = roleService.getRoleList(cacheStationCompanyId);

        if (stationRoleList != null && stationRoleList.size() > 0) {
            for (StationRoleDTO stationRoleDTO : stationRoleList) {

                StationRolesVO vo = new StationRolesVO();
                vo.setId(stationRoleDTO.getId());
                vo.setRoleName(stationRoleDTO.getRoleName());
                resultList.add(vo);
            }
        }

        return resultList;
    }

    /**
     * 系统-角色管理-编辑/新增-根据角色ID获取所有菜单及权限集合
     *
     * @param roleId 新增-不需传入  编辑-需要传入
     * @return
     */
    @RequestMapping(value = "/stationRole/menus/permissions", method = RequestMethod.GET)
    @ApiOperation(value = "根据角色ID获取所有菜单及权限集合")
    public List<StationMenuDTO> listMenuAndPermission(@RequestParam(value = "roleId", required = false) Integer roleId) {
        return stationMenuService.listMenuAndPermission(roleId);
    }

}
