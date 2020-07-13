package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.pojo.dto.user.RoleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.Role;
import com.yimao.cloud.user.service.MenuService;
import com.yimao.cloud.user.service.RoleService;
import io.swagger.annotations.Api;
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
    private RoleService roleService;
    @Resource
    private MenuService menuService;

    /**
     * 创建角色
     *
     * @param dto 角色信息
     * @return
     */
    @RequestMapping(value = "/role", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void save(@RequestParam Integer sysType, @RequestBody RoleDTO dto) {
        Role role = new Role(dto);
        role.setSysType(sysType);
        roleService.save(role, dto.getPermissionIds());
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return
     */
    @RequestMapping(value = "/role/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer id) {
        roleService.delete(id);
    }

    /**
     * 更新角色
     *
     * @param dto 角色信息
     * @return
     */
    @RequestMapping(value = "/role", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody RoleDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            Role role = new Role(dto);
            roleService.update(role, dto.getPermissionIds());
        }
    }

    /**
     * 查询角色列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @return
     */
    @RequestMapping(value = "/roles/{pageNum}/{pageSize}", method = RequestMethod.GET)
    public PageVO<RoleDTO> page(@PathVariable Integer pageNum,
                                @PathVariable Integer pageSize,
                                @RequestParam(required = false) String roleName,
                                @RequestParam Integer sysType) {
        return roleService.page(pageNum, pageSize, roleName, sysType);
    }

    /**
     * 根据角色ID获取所有菜单及权限集合
     *
     * @return
     */
    @RequestMapping(value = "/role/menus/permissions", method = RequestMethod.GET)
    public List<MenuDTO> listMenuAndPermission(@RequestParam Integer sysType, @RequestParam(required = false) Integer roleId) {
        return menuService.listMenuAndPermission(sysType, roleId);
    }

}
