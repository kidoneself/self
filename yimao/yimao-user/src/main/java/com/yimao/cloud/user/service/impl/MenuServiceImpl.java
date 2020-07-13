package com.yimao.cloud.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.user.MenuDTO;
import com.yimao.cloud.pojo.dto.user.PermissionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.mapper.MenuMapper;
import com.yimao.cloud.user.mapper.RoleMenuMapper;
import com.yimao.cloud.user.mapper.RolePermissionMapper;
import com.yimao.cloud.user.po.Menu;
import com.yimao.cloud.user.po.RoleMenu;
import com.yimao.cloud.user.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2018/12/21.
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private UserCache userCache;
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;

    /**
     * 添加菜单
     *
     * @param menu 菜单信息
     */
    @Override
    public void save(Menu menu) {
        if (StringUtil.isBlank(menu.getName())) {
            throw new BadRequestException("菜单名不能为空。");
        }
        if (StringUtil.isBlank(menu.getUrl())) {
            throw new BadRequestException("菜单地址不能为空。");
        }
        if (StringUtil.isBlank(menu.getIcon())) {
            throw new BadRequestException("菜单图标不能为空。");
        }
        if (menu.getPid() == null || menu.getPid() == 0) {
            menu.setPid(0);
            menu.setLevel(1);//一级菜单
        } else {
            Menu parent = menuMapper.selectByPrimaryKey(menu.getPid());
            if (Objects.isNull(parent)) {
                throw new YimaoException("添加菜单失败，未找到父级菜单。");
            }
            // 设置菜单层级
            menu.setLevel(parent.getLevel() + 1);
        }
        //一级菜单图标为必填，其它为非必填
        if (menu.getLevel() == 1 && StringUtil.isBlank(menu.getIcon())) {
            throw new BadRequestException("菜单图标不能为空。");
        }
        Menu query = new Menu();
        query.setName(menu.getName());
        query.setSysType(menu.getSysType());
        int count = menuMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("菜单已存在。");
        }
        query.setName(null);
        query.setUrl(menu.getUrl());
        query.setSysType(menu.getSysType());
        count = menuMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("菜单URL已存在。");
        }
        menu.setCreator(userCache.getCurrentAdminRealName());
        menu.setCreateTime(new Date());
        int result = menuMapper.insert(menu);
        if (result < 1) {
            throw new YimaoException("添加菜单失败。");
        }
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void delete(Integer id) {
        boolean exists = menuMapper.existsWithPrimaryKey(id);
        if (!exists) {
            throw new NotFoundException("未找到菜单信息。");
        }

        int result = menuMapper.deleteByPrimaryKey(id);
        if (result < 1) {
            throw new YimaoException("删除菜单失败。");
        }

        // 删除角色菜单关联表数据
        RoleMenu roleMenu = new RoleMenu();
        roleMenu.setMenuId(id);
        roleMenuMapper.delete(roleMenu);
    }

    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     */
    @Override
    public void update(Menu menu) {
        Menu record = menuMapper.selectByPrimaryKey(menu.getId());
        if (record == null) {
            throw new BadRequestException("修改对象不存在。");
        }
        if (StringUtil.isBlank(menu.getName())) {
            throw new BadRequestException("菜单名不能为空。");
        }
        if (StringUtil.isBlank(menu.getUrl())) {
            throw new BadRequestException("菜单地址不能为空。");
        }
        if (menu.getPid() == null || menu.getPid() == 0) {
            menu.setPid(0);
            menu.setLevel(1);//一级菜单
        } else {
            Menu parent = menuMapper.selectByPrimaryKey(menu.getPid());
            if (Objects.isNull(parent)) {
                throw new YimaoException("修改菜单失败，未找到父级菜单。");
            }
            // 设置菜单层级
            menu.setLevel(parent.getLevel() + 1);
        }
        //一级菜单图标为必填，其它为非必填
        if (menu.getLevel() == 1 && StringUtil.isBlank(menu.getIcon())) {
            throw new BadRequestException("菜单图标不能为空。");
        }
        //校验菜单名称是否已经存在
        Example example = new Example(Menu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", menu.getId());
        criteria.andEqualTo("name", menu.getName());
        criteria.andEqualTo("sysType", record.getSysType());
        int count = menuMapper.selectCountByExample(example);
        if (count > 0) {
            throw new BadRequestException("该菜单名称已存在。");
        }
        //校验菜单URL是否已经存在
        Example example2 = new Example(Menu.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andNotEqualTo("id", menu.getId());
        criteria2.andEqualTo("url", menu.getUrl());
        criteria2.andEqualTo("sysType", record.getSysType());
        count = menuMapper.selectCountByExample(example2);
        if (count > 0) {
            throw new BadRequestException("该菜单URL已存在。");
        }
        menu.setUpdater(userCache.getCurrentAdminRealName());
        menu.setUpdateTime(new Date());
        //不更新sysType
        menu.setSysType(null);
        menuMapper.updateByPrimaryKeySelective(menu);
    }

    /**
     * 分页查询菜单信息
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @return
     */
    @Override
    public PageVO<MenuDTO> page(Integer pageNum, Integer pageSize, Integer sysType, Integer id) {
        if (Objects.nonNull(id)) {
            // 分页查询
            PageHelper.startPage(pageNum, pageSize);
            Page<MenuDTO> page = menuMapper.selectSubMenus(id, sysType);
            return new PageVO<>(pageNum, page);
        } else {
            // 分页查询
            Example example = new Example(Menu.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("sysType", sysType);
            example.orderBy("sorts");
            PageHelper.startPage(pageNum, pageSize);
            Page<Menu> page = (Page<Menu>) menuMapper.selectByExample(example);
            // PO对象转成DTO对象
            return new PageVO<>(pageNum, page, Menu.class, MenuDTO.class);
        }
    }

    /**
     * 获取级联菜单结构
     */
    @Override
    public List<MenuDTO> listMenuTree(Integer sysType) {
        List<MenuDTO> allMenus = menuMapper.selectAllMenus(sysType);
        //获取该角色下所有菜单
        List<MenuDTO> firstLevelMenus = menuMapper.selectFirstLevelMenus(sysType);

        //为一级菜单设置子菜单准备递归
        for (MenuDTO menu : firstLevelMenus) {
            //获取父菜单下所有子菜单调用getChildList
            List<MenuDTO> childList = this.getChildList(menu.getId(), allMenus);
            menu.setSubMenus(childList);
        }
        return firstLevelMenus;
    }

    /**
     * 递归组合子菜单逻辑
     *
     * @param pid      父级菜单ID
     * @param allMenus 所有菜单集合
     * @return
     */
    @Override
    public List<MenuDTO> getChildList(Integer pid, List<MenuDTO> allMenus) {
        //构建子菜单
        List<MenuDTO> subMenus = new ArrayList<>();
        //遍历传入的list
        ListIterator<MenuDTO> listIterator = allMenus.listIterator();
        while (listIterator.hasNext()) {
            MenuDTO menuDTO = listIterator.next();
            //所有菜单的pid与传入的节点id比较，若相等则为该级菜单的子菜单
            if (Objects.equals(menuDTO.getPid(), pid)) {
                subMenus.add(menuDTO);
                listIterator.remove();
            }
        }
        //递归
        for (MenuDTO menuDTO : subMenus) {
            menuDTO.setSubMenus(this.getChildList(menuDTO.getId(), allMenus));
        }
        if (subMenus.size() == 0) {
            return null;
        }
        return subMenus;
    }

    /**
     * 获取所有子菜单ID（包含自己）
     *
     * @param sysType 系统分类
     * @param menuId  父级菜单ID
     */
    @Override
    public List<Integer> getChildMenuIds(Integer sysType, Integer menuId) {
        List<MenuDTO> allMenus = menuMapper.selectAllMenus(sysType);
        //构建子菜单
        List<Integer> subMenus = new ArrayList<>();
        subMenus.add(menuId);
        this.getChildList(menuId, allMenus, subMenus);
        return subMenus;
    }

    /**
     * 递归获取子菜单逻辑
     *
     * @param pid      父菜单ID
     * @param allMenus 所有菜单
     * @param subMenus 子菜单集合
     */
    private void getChildList(Integer pid, List<MenuDTO> allMenus, List<Integer> subMenus) {
        List<Integer> tempList = new ArrayList<>();
        //遍历传入的list
        ListIterator<MenuDTO> listIterator = allMenus.listIterator();
        while (listIterator.hasNext()) {
            MenuDTO menu = listIterator.next();
            //所有菜单的pid与传入的节点id比较，若相等则为该级菜单的子菜单
            if (Objects.equals(menu.getPid(), pid)) {
                tempList.add(menu.getId());
                listIterator.remove();
            }
        }
        //递归
        for (Integer id : tempList) {
            this.getChildList(id, allMenus, subMenus);
        }
        subMenus.addAll(tempList);
    }

    /**
     * 根据角色ID获取所有菜单及权限集合
     *
     * @param roleId 角色ID
     * @return
     */
    @Override
    public List<MenuDTO> listMenuAndPermission(Integer sysType, Integer roleId) {
        // 获取所有菜单列表（每个菜单中携带对应的权限列表）
        List<MenuDTO> menus = menuMapper.selectMenuWithPermission(sysType);
        // 获取一共有几级菜单
        Integer maxLevel = menuMapper.selectMaxMenuLevel(sysType);

        // 获取该角色对应的所有权限
        Set<Integer> permissionIds = rolePermissionMapper.selectPermissionIdsByRoleId(roleId);

        if (CollectionUtil.isNotEmpty(menus)) {
            Map<Integer, List<MenuDTO>> map = new HashMap<>();
            // 1-将菜单分级存放
            for (int i = 1; i <= maxLevel; i++) {
                map.put(i, new ArrayList<>());
                for (MenuDTO menuDTO : menus) {
                    if (menuDTO.getLevel() == i) {
                        map.get(i).add(menuDTO);
                    }
                    // 菜单的权限列表不为空 && 角色拥有的权限列表不为空
                    if (CollectionUtil.isNotEmpty(menuDTO.getPermissions()) && CollectionUtil.isNotEmpty(permissionIds)) {
                        for (PermissionDTO permissionDTO : menuDTO.getPermissions()) {
                            // 判断菜单的权限是否被包含在角色拥有的权限列表中，如果被包含，则设置个标识（前端展示用到）
                            if (permissionIds.contains(permissionDTO.getId())) {
                                // 表示该角色拥有该权限
                                permissionDTO.setHave(true);
                                // 表示该角色拥有该菜单
                                menuDTO.setHave(true);
                            }
                        }
                    }
                }
            }
            // 2-将子菜单设置到对应的上级菜单下
            for (int i = maxLevel; i > 1; i--) {
                List<MenuDTO> outer = map.get(i - 1);
                List<MenuDTO> inner = map.get(i);
                for (MenuDTO outerDto : outer) {
                    List<MenuDTO> subMenus = new ArrayList<>();
                    for (MenuDTO innerDto : inner) {
                        if (Objects.equals(innerDto.getPid(), outerDto.getId())) {
                            subMenus.add(innerDto);
                        }
                        // 如果某个子菜单被该角色拥有，则上级菜单也应该被角色拥有
                        if (innerDto.getHave() != null && innerDto.getHave()) {
                            outerDto.setHave(innerDto.getHave());
                        }
                    }
                    outerDto.setSubMenus(subMenus);
                }
            }
            return map.get(1);
        }
        return null;
    }

}
