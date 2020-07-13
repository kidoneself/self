package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.user.PermissionCacheDTO;
import com.yimao.cloud.pojo.dto.user.PermissionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.mapper.PermissionMapper;
import com.yimao.cloud.user.po.Permission;
import com.yimao.cloud.user.service.PermissionService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/3.
 */
@RestController
@Api(tags = "PermissionController")
public class PermissionController {

    @Resource
    private PermissionService permissionService;
    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 创建权限
     *
     * @param dto 权限信息
     */
    @PostMapping(value = "/permission")
    public void save(@RequestParam Integer sysType, @RequestBody PermissionDTO dto) {
        Permission permission = new Permission(dto);
        permission.setSysType(sysType);
        permissionService.save(permission);
    }

    /**
     * 删除权限账号
     *
     * @param id 权限ID
     */
    @DeleteMapping(value = "/permission/{id}")
    public void delete(@PathVariable Integer id) {
        permissionService.delete(id);
    }

    /**
     * 更新权限
     *
     * @param dto 权限信息
     */
    @PutMapping(value = "/permission")
    public void update(@RequestBody PermissionDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            Permission permission = new Permission(dto);
            permissionService.update(permission);
        }
    }

    /**
     * 查询权限列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param menuId   菜单ID
     */
    @GetMapping(value = "/permissions/{pageNum}/{pageSize}")
    public PageVO<PermissionDTO> page(@PathVariable Integer pageNum,
                                      @PathVariable Integer pageSize,
                                      @RequestParam Integer sysType,
                                      @RequestParam(required = false) Integer menuId) {
        return permissionService.page(pageNum, pageSize, sysType, menuId);
    }

    /**
     * 查询权限列表
     *
     * @param sysType 系统类型
     */
    @GetMapping(value = "/permissions")
    public List<PermissionCacheDTO> listPermissionBySysType(@RequestParam Integer sysType) {
        return permissionMapper.selectBySysType(sysType);
    }

}
