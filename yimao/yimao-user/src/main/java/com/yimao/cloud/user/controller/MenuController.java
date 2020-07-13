package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.pojo.dto.user.PermissionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.Menu;
import com.yimao.cloud.user.service.MenuService;
import com.yimao.cloud.user.service.PermissionService;
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
 * 系统菜单控制器
 *
 * @author Zhang Bo
 * @date 2018/11/3.
 */
@RestController
public class MenuController {

    @Resource
    private MenuService menuService;
    @Resource
    private PermissionService permissionService;

    /**
     * 添加菜单
     *
     * @param dto 菜单信息
     */
    @RequestMapping(value = "/menu", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void save(@RequestParam Integer sysType, @RequestBody MenuDTO dto) {
        Menu menu = new Menu(dto);
        menu.setSysType(sysType);
        menuService.save(menu);
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    @RequestMapping(value = "/menu/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer id) {
        menuService.delete(id);
    }

    /**
     * 更新菜单
     *
     * @param dto 部门信息
     */
    @RequestMapping(value = "/menu", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody MenuDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            Menu menu = new Menu(dto);
            menuService.update(menu);
        }
    }

    /**
     * 查询菜单列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    @RequestMapping(value = "/menus/{pageNum}/{pageSize}", method = RequestMethod.GET)
    public PageVO<MenuDTO> page(@PathVariable Integer pageNum,
                                @PathVariable Integer pageSize,
                                @RequestParam Integer sysType,
                                @RequestParam(required = false) Integer id) {
        return menuService.page(pageNum, pageSize, sysType, id);
    }

    /**
     * 获取级联菜单结构
     */
    @RequestMapping(value = "/menu/tree", method = RequestMethod.GET)
    public List<MenuDTO> listMenuTree(@RequestParam Integer sysType) {
        return menuService.listMenuTree(sysType);
    }

    /**
     * 根据菜单ID获取权限列表
     */
    @RequestMapping(value = "/menu/permissions", method = RequestMethod.GET)
    public List<PermissionDTO> listPermissionByMenuId(@RequestParam(required = false) Integer menuId) {
        return permissionService.listByMenuId(menuId);
    }

}
